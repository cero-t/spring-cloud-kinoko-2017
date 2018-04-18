package ninja.cero.ecommerce.stock.client;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.stock.domain.Stock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StockClient {
	private static final String STOCK_URL = "http://stock-service";

	WebClient webClient;

	public StockClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Flux<Stock> findAll() {
		return webClient.get().uri(STOCK_URL).retrieve().bodyToFlux(Stock.class);
	}

	public Flux<Stock> findByIds(Collection<Long> ids) {
		String idString = ids.stream().map(id -> id.toString()).collect(Collectors.joining(","));
		return webClient.get().uri(STOCK_URL + "/" + idString).retrieve().bodyToFlux(Stock.class);
	}

	public Mono<Void> keepStock(List<Stock> keeps) {
		return webClient.post().uri(STOCK_URL).syncBody(keeps).retrieve().bodyToMono(Void.class);
	}
}
