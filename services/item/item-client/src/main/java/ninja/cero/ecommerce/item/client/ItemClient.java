package ninja.cero.ecommerce.item.client;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.item.domain.Item;

public class ItemClient {
	private static final String ITEM_URL = "http://item-service";

	WebClient webClient;

	public ItemClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public List<Item> findAll() {
		return webClient.get().uri(ITEM_URL).retrieve().bodyToFlux(Item.class).collectList().block();
	}

	public List<Item> findByIds(Collection<Long> ids) {
		String idString = ids.stream().map(id -> id.toString()).collect(Collectors.joining(","));
		return webClient.get().uri(ITEM_URL + "/" + idString).retrieve().bodyToFlux(Item.class).collectList().block();
	}
}
