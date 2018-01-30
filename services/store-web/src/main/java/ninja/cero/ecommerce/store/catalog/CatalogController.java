package ninja.cero.ecommerce.store.catalog;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ninja.cero.ecommerce.item.client.ItemClient;
import ninja.cero.ecommerce.item.domain.Item;
import ninja.cero.ecommerce.stock.client.StockClient;
import ninja.cero.ecommerce.stock.domain.Stock;

@RestController
@RequestMapping("/catalog")
@CrossOrigin
public class CatalogController {
	@Autowired
	ItemClient itemClient;

	@Autowired
	StockClient stockClient;

	@GetMapping
	public List<CatalogItem> findCatalog() {
		List<Item> items = itemClient.findAll();
		List<Stock> stocks = stockClient.findByIds(items.stream().map(i -> i.id).collect(Collectors.toList()));

		Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(s -> s.itemId, s -> s.quantity));

		// Filter items by stock
		List<CatalogItem> catalogItems = items.stream().map(item -> {
			CatalogItem catalogItem = new CatalogItem();
			catalogItem.id = item.id;
			catalogItem.name = item.name;
			catalogItem.media = item.media;
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
