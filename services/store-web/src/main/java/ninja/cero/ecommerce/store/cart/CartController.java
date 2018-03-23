package ninja.cero.ecommerce.store.cart;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ninja.cero.ecommerce.cart.client.CartClient;
import ninja.cero.ecommerce.cart.domain.Cart;
import ninja.cero.ecommerce.cart.domain.CartDetail;
import ninja.cero.ecommerce.cart.domain.CartEvent;
import ninja.cero.ecommerce.stock.client.StockClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {
	@Autowired
	CartClient cartClient;

	@Autowired
	StockClient stockClient;

	@PostMapping
	public Mono<Cart> createCart() {
		return cartClient.createCart();
	}

	@GetMapping("/{cartId}")
	public Mono<CartDetail> findCart(@PathVariable String cartId) {
		return cartClient.findCartDetailById(cartId);
	}

	@PostMapping("/{cartId}")
	public Mono<CartDetail> addEvent(@PathVariable String cartId, @RequestBody CartEvent cartEvent) {
		return cartClient.findCartById(cartId)
				.switchIfEmpty(Mono.error(new RuntimeException("No valid cart")))
				.flatMap(x -> stockClient.findByIds(Arrays.asList(cartEvent.itemId)).next())
				.switchIfEmpty(Mono.error(new RuntimeException("No stock info!")))
				.flatMap(stock -> {
					if (stock.quantity < cartEvent.quantity) {
						throw new RuntimeException("Not enough stock!");
					}
					return cartClient.addItem(cartId, cartEvent);
				})
				.flatMap(x -> cartClient.findCartDetailById(cartId));
	}

	@DeleteMapping("/{cartId}/{itemId}")
	public Mono<CartDetail> removeItem(@PathVariable String cartId, @PathVariable Long itemId) {
		cartClient.removeItem(cartId, itemId);
		return cartClient.findCartDetailById(cartId);
	}
}
