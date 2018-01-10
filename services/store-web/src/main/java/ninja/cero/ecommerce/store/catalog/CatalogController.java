package ninja.cero.ecommerce.store.catalog;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.item.client.ItemClient;
import ninja.cero.ecommerce.item.domain.Item;
import ninja.cero.ecommerce.stock.domain.Stock;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
	private static final String STOCK_URL = "http://stock-service";

    @Autowired
    WebClient webClient;

    @Autowired
    ItemClient itemClient;
    
	@GetMapping
	public List<CatalogItem> findCatalog(HttpSession session) {
		// Force create session
		session.getId();

        List<Item> items = itemClient.findAll();

		// Get item stocks
		String itemIds = items.stream().map(i -> i.id.toString()).collect(Collectors.joining(","));

        List<Stock> stocks = webClient.get()
    	        .uri(STOCK_URL + "/" + itemIds)
    	        .retrieve()
    	        .bodyToFlux(Stock.class)
    	        .collectList()
    	        .block();

        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(s -> s.itemId, s -> s.quantity));

		// Filter items by stock
		List<CatalogItem> catalogItems = items.stream().map(item -> {
			CatalogItem catalogItem = new CatalogItem();
			catalogItem.id = item.id;
			catalogItem.name = item.name;
			catalogItem.author = item.author;
			catalogItem.release = item.release;
			catalogItem.unitPrice = item.unitPrice;
			catalogItem.image = item.image;
			catalogItem.inStock = stockMap.getOrDefault(item.id, 0) > 0;
			return catalogItem;
		}).collect(Collectors.toList());

		return catalogItems;
	}
}
