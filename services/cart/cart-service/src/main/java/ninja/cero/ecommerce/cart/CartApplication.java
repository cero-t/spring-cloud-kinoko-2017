package ninja.cero.ecommerce.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringCloudApplication
@EnableJdbcRepositories
public class CartApplication {
	public static void main(String[] args) {
		SpringApplication.run(CartApplication.class, args);
	}
}
