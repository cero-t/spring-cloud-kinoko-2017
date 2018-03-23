package ninja.cero.ecommerce.cart.app;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ninja.cero.ecommerce.cart.domain.Cart;
import ninja.cero.ecommerce.cart.domain.CartDetail;
import ninja.cero.ecommerce.cart.domain.CartEvent;
import ninja.cero.ecommerce.cart.domain.CartItem;
import ninja.cero.ecommerce.item.client.ItemClient;
import ninja.cero.ecommerce.item.domain.Item;
import reactor.core.publisher.Mono;

@RestController
public class CartController {
	@Autowired
	CartRepository cartRepository;

	@Autowired
	ItemClient itemClient;

	ObjectMapper mapper = new ObjectMapper();

	@GetMapping
	public List<Cart> findAll() {
		return StreamSupport.stream(cartRepository.findAll().spliterator(), false).map(this::toCart)
				.collect(Collectors.toList());
	}

	@GetMapping("/{cartId}")
	public Optional<Cart> findCartById(@PathVariable Long cartId) {
		return cartRepository.findById(cartId).map(this::toCart);
	}

	@GetMapping("/{cartId}/detail")
	public Mono<CartDetail> findCartDetailById(@PathVariable Long cartId) throws InterruptedException {
		// Create cart
		CartEntity entity = cartRepository.findById(cartId).get();
		Cart cart = toCart(entity);

		// Find items in cart and convert to map
		return itemClient.findByIds(cart.items.keySet()).collectList().map(items -> {
			CartDetail cartDetail = new CartDetail();
			cartDetail.cartId = entity.id;

			Map<Long, Item> itemMap = items.stream().collect(Collectors.toMap(i -> i.id, i -> i));

			// Resolve cart items
			cartDetail.items = cart.items.entrySet().stream().map(i -> {
				Item item = itemMap.get(i.getKey());
				if (item == null) {
					return null;
				}

				CartItem cartItem = new CartItem();
				cartItem.itemId = item.id;
				cartItem.name = item.name;
				cartItem.author = item.author;
				cartItem.release = item.release;
				cartItem.unitPrice = item.unitPrice;
				cartItem.image = item.image;
				cartItem.quantity = i.getValue();
				return cartItem;
			}).collect(Collectors.toList());

			cartDetail.total = cartDetail.items.stream().map(i -> i.unitPrice.multiply(new BigDecimal(i.quantity)))
					.reduce((b1, b2) -> b1.add(b2)).orElse(BigDecimal.ZERO);
			return cartDetail;
		});
	}

	private Cart toCart(CartEntity entity) {
		Cart cart = new Cart();
		cart.cartId = entity.id;
		try {
			Map<?, ?> map = mapper.readValue(entity.items, Map.class);
			cart.items = map.entrySet().stream().collect(Collectors.toMap(e -> Long.valueOf(e.getKey().toString()),
					e -> Integer.valueOf(e.getValue().toString())));
		} catch (IOException ex) {
			throw new RuntimeException("Json deserialzie error", ex);
		}
		return cart;
	}

	private CartEntity toEntity(Cart cart) {
		CartEntity entity = new CartEntity();
		entity.id = cart.cartId;
		try {
			entity.items = mapper.writeValueAsString(cart.items);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Json serialzie error", ex);
		}
		return entity;
	}

	@PostMapping
	public Cart createCart() {
		Cart cart = new Cart();
		CartEntity save = cartRepository.save(toEntity(cart));
		cart.cartId = save.id;
		return cart;
	}

	@PostMapping("/{cartId}")
	public Mono<Cart> addItem(@PathVariable Long cartId, @RequestBody CartEvent cartEvent) {
		Cart cart = cartRepository.findById(cartId).map(this::toCart)
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		cart.items.compute(cartEvent.itemId, (key, old) -> old == null ? cartEvent.quantity : old + cartEvent.quantity);
		cartRepository.save(toEntity(cart));

		return Mono.just(cart);
	}

	@DeleteMapping("/{cartId}/items/{itemId}")
	public Mono<Cart> removeItem(@PathVariable Long cartId, @PathVariable Long itemId) {
		Cart cart = cartRepository.findById(cartId).map(this::toCart)
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		cart.items.remove(itemId);
		cartRepository.save(toEntity(cart));

		return Mono.just(cart);
	}
}
