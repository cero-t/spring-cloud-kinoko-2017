package ninja.cero.ecommerce.cart.app;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ninja.cero.ecommerce.cart.domain.Cart;
import ninja.cero.ecommerce.cart.domain.CartDetail;
import ninja.cero.ecommerce.cart.domain.CartEvent;
import ninja.cero.ecommerce.cart.domain.CartItem;
import ninja.cero.ecommerce.item.client.ItemClient;
import ninja.cero.ecommerce.item.domain.Item;

@RestController
public class CartController {
	@Autowired
	CartRepository cartRepository;

	@Autowired
	ItemClient itemClient;

	@GetMapping
	public Iterable<Cart> findAll() {
		return cartRepository.findAll();
	}

	@GetMapping("/{cartId}")
	public Optional<Cart> findCartById(@PathVariable String cartId) {
		return cartRepository.findById(cartId);
	}

	@GetMapping("/{cartId}/detail")
	public CartDetail findCartDetailById(@PathVariable String cartId) {
		// Create cart
		Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));

		CartDetail cartDetail = new CartDetail();
		cartDetail.cartId = cart.cartId;

		// Find items in cart and convert to map
		List<Item> items = itemClient.findByIds(cart.items.keySet());
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
		}).collect(Collectors.toMap(i -> i.itemId, i -> i));

		// Count amount
		cartDetail.amount = cartDetail.items.values().stream()
				.map(i -> i.unitPrice.multiply(new BigDecimal(i.quantity))).reduce((b1, b2) -> b1.add(b2))
				.orElse(BigDecimal.ZERO);

		return cartDetail;
	}

	@PostMapping
	public Cart createCart() {
		Cart cart = new Cart();
		cartRepository.save(cart);

		return cart;
	}

	@PostMapping("/{cartId}")
	public Cart addItem(@PathVariable String cartId, @RequestBody CartEvent cartEvent) {
		Cart cart = cartRepository.findById(cartId).orElse(new Cart());

		cart.items.compute(cartEvent.itemId, (key, old) -> old == null ? cartEvent.quantity : old + cartEvent.quantity);
		cartRepository.save(cart);

		return cart;
	}

	@DeleteMapping("/{cartId}/items/{itemId}")
	public Cart removeItem(@PathVariable String cartId, @PathVariable Long itemId) {
		Cart cart = cartRepository.findById(cartId).orElse(new Cart());

		cart.items.remove(itemId);
		cartRepository.save(cart);

		return cart;
	}
}
