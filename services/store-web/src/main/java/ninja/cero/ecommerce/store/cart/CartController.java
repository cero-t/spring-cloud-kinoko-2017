package ninja.cero.ecommerce.store.cart;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import ninja.cero.ecommerce.stock.domain.Stock;
import ninja.cero.ecommerce.store.UserContext;

@RestController
@RequestMapping("/cart")
public class CartController {
	@Autowired
	CartClient cartClient;

	@Autowired
	StockClient stockClient;

	@Autowired
	UserContext userContext;

	@GetMapping
	public CartDetail findCart() {
		// Get cart
		if (userContext.cartId == null) {
			return null;
		}

		return cartClient.findCartDetailById(userContext.cartId).get();
	}

	@PostMapping("/items")
	public Cart addItem(@RequestBody CartEvent cartEvent) {
		if (userContext.cartId == null) {
			Cart cart = cartClient.createCart();
			userContext.cartId = cart.cartId;
		}

		List<Stock> stocks = stockClient.findByIds(Arrays.asList(cartEvent.itemId));

		if (stocks.size() == 0 || stocks.get(0).quantity < cartEvent.quantity) {
			throw new RuntimeException("Not enough stock!");
		}

		return cartClient.addItem(userContext.cartId, cartEvent);
	}

	@DeleteMapping("/items/{itemId}")
	public Cart removeItem(@PathVariable Long itemId) {
		if (userContext.cartId == null) {
			return null;
		}

		cartClient.removeItem(userContext.cartId, itemId);
		return cartClient.findCartById(userContext.cartId).get();
	}
}
