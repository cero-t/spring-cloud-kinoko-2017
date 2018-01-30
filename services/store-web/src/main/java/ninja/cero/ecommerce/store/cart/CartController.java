package ninja.cero.ecommerce.store.cart;

import java.util.Arrays;
import java.util.List;

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
import ninja.cero.ecommerce.stock.domain.Stock;

@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {
	@Autowired
	CartClient cartClient;

	@Autowired
	StockClient stockClient;

	@PostMapping
	public Cart createCart() {
		return cartClient.createCart();
	}

	@GetMapping("/{cartId}")
	public CartDetail findCart(@PathVariable String cartId) {
		return cartClient.findCartDetailById(cartId).get();
	}

	@PostMapping("/{cartId}")
	public CartDetail addEvent(@PathVariable String cartId, @RequestBody CartEvent cartEvent) {
		cartClient.findCartById(cartId).orElseThrow(() -> new RuntimeException("Cart not found")); 

		List<Stock> stocks = stockClient.findByIds(Arrays.asList(cartEvent.itemId));
		if (stocks.size() == 0 || stocks.get(0).quantity < cartEvent.quantity) {
			throw new RuntimeException("Not enough stock!");
		}

		cartClient.addItem(cartId, cartEvent);
		return cartClient.findCartDetailById(cartId).get();
	}

	@DeleteMapping("/{cartId}/{itemId}")
	public CartDetail removeItem(@PathVariable String cartId, @PathVariable Long itemId) {
		cartClient.removeItem(cartId, itemId);
		return cartClient.findCartDetailById(cartId).get();
	}
}
