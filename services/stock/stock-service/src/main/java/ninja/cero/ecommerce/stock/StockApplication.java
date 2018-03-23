package ninja.cero.ecommerce.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringCloudApplication
@EnableJdbcRepositories
public class StockApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockApplication.class, args);
	}
}
