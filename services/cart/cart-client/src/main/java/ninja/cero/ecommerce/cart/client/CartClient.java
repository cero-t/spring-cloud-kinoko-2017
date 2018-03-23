package ninja.cero.ecommerce.cart.client;

import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.cart.domain.Cart;
import ninja.cero.ecommerce.cart.domain.CartDetail;
import ninja.cero.ecommerce.cart.domain.CartEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CartClient {
	private static final String CART_URL = "http://cart-service";

	WebClient webClient;

	public CartClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Flux<Cart> findAll() {
		return webClient.get().uri(CART_URL).retrieve().bodyToFlux(Cart.class);
	}

	public Mono<Cart> findCartById(String cartId) {
		return webClient.get().uri(CART_URL + "/" + cartId).retrieve().bodyToMono(Cart.class);
	}

	public Mono<CartDetail> findCartDetailById(String cartId) {
		return webClient.get().uri(CART_URL + "/" + cartId + "/" + "detail").retrieve().bodyToMono(CartDetail.class);
	}

	public Mono<Cart> createCart() {
		return webClient.post().uri(CART_URL).retrieve().bodyToMono(Cart.class);
	}

	public Mono<Cart> addItem(String cartId, CartEvent cartEvent) {
		return webClient.post().uri(CART_URL + "/" + cartId).syncBody(cartEvent).retrieve().bodyToMono(Cart.class);
	}

	public Mono<Cart> removeItem(String cartId, Long itemId) {
		return webClient.delete().uri(CART_URL + "/" + cartId + "/" + "items" + "/" + itemId).retrieve()
				.bodyToMono(Cart.class);
	}
}
