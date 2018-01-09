package ninja.cero.ecommerce.store.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.cart.domain.Cart;
import ninja.cero.ecommerce.cart.domain.CartDetail;
import ninja.cero.ecommerce.cart.domain.CartEvent;
import ninja.cero.ecommerce.stock.domain.Stock;
import ninja.cero.ecommerce.store.UserContext;

@RestController
@RequestMapping("/cart")
public class CartController {
	private static final String CART_URL = "http://cart-service";
	private static final String STOCK_URL = "http://stock-service";

	@Autowired
	WebClient webClient;

	@Autowired
	UserContext userContext;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public CartDetail findCart() {
		// Get cart
		if (userContext.cartId == null) {
			return null;
		}

		return webClient.get()
			.uri(CART_URL + "/" + userContext.cartId + "/detail")
			.retrieve()
			.bodyToMono(CartDetail.class)
			.block();
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	public Cart addItem(@RequestBody CartEvent cartEvent) {
		if (userContext.cartId == null) {
			Cart cart = webClient.post()
				.uri(CART_URL)
				.retrieve()
				.bodyToMono(Cart.class)
				.block();
			userContext.cartId = cart.cartId;
		}

		Stock stock = webClient.get()
			.uri(STOCK_URL + "/" + cartEvent.itemId)
			.retrieve()
			.bodyToFlux(Stock.class)
			.blockFirst();
		
		if (stock.quantity < cartEvent.quantity) {
			throw new RuntimeException("Not enough stock!");
		}

		return webClient.post()
			.uri(CART_URL + "/" + userContext.cartId)
			.syncBody(cartEvent)
			.retrieve()
			.bodyToMono(Cart.class)
			.block();
	}

	@RequestMapping(value = "/items/{itemId}", method = RequestMethod.DELETE)
	public Cart removeItem(@PathVariable String itemId) {
		if (userContext.cartId == null) {
			return null;
		}

		webClient.delete()
			.uri(CART_URL + "/" + userContext.cartId + "/items/" + itemId)
			.exchange();

		return webClient.get()
			.uri(CART_URL + "/" + userContext.cartId)
			.retrieve()
			.bodyToMono(Cart.class)
			.block();
	}
}
