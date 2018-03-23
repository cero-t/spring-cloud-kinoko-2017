package ninja.cero.ecommerce.stock.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ninja.cero.ecommerce.stock.domain.Stock;

@RestController
public class StockController {
	@Autowired
	StockRepository stockRepository;

	@GetMapping
	public Iterable<Stock> findAll() {
		return stockRepository.findAll();
	}

	@GetMapping("/{ids}")
	public Iterable<Stock> findByIds(@PathVariable List<Long> ids) {
		return stockRepository.findAllById(ids);
	}

	@PostMapping
	public void keepStock(@RequestBody List<Stock> keeps) {
		keeps.stream().forEach(s -> {
			int count = stockRepository.subtractIfPossible(s.itemId, s.quantity);
			if (count == 0) {
				throw new RuntimeException("Not enough stocks.");
			}
		});
	}
}
