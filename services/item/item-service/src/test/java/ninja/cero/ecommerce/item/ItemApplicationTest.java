package ninja.cero.ecommerce.item;

import java.time.Duration;
import java.util.List;

import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.item.domain.Item;

public class ItemApplicationTest {
	@Test
	public void test() {
		WebClient client = WebClient.builder().build();
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			List<Item> result = client.get().uri("http://localhost:9001/1,2").retrieve().bodyToFlux(Item.class)
					.collectList().block(Duration.ofSeconds(10));
			System.out.println(result);
		}
	}
}
