package ninja.cero.ecommerce.cart.client;

import java.util.List;
import java.util.Optional;

import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.cart.domain.Cart;
import ninja.cero.ecommerce.cart.domain.CartDetail;
import ninja.cero.ecommerce.cart.domain.CartEvent;

public class CartClient {
	private static final String CART_URL = "http://cart-service";

	WebClient webClient;

	public CartClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public List<Cart> findAll() {
		return webClient.get().uri(CART_URL).retrieve().bodyToFlux(Cart.class).collectList().block();
	}

	public Optional<Cart> findCartById(String cartId) {
		return webClient.get().uri(CART_URL, cartId).retrieve().bodyToMono(Cart.class).blockOptional();
	}

	public Optional<CartDetail> findCartDetailById(String cartId) {
		return webClient.get().uri(CART_URL + "/" + cartId + "/" + "detail").retrieve().bodyToMono(CartDetail.class)
				.blockOptional();
	}

	public Cart createCart() {
		return webClient.post().uri(CART_URL).retrieve().bodyToMono(Cart.class).block();
	}

	public Cart addItem(String cartId, CartEvent cartEvent) {
		return webClient.post().uri(CART_URL + "/" + cartId).syncBody(cartEvent).retrieve().bodyToMono(Cart.class)
				.block();
	}

	public Cart removeItem(String cartId, Long itemId) {
		return webClient.delete().uri(CART_URL + "/" + cartId + "/" + "items" + "/" + itemId).retrieve()
				.bodyToMono(Cart.class).block();
	}
}
