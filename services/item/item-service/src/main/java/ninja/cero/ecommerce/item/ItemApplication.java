package ninja.cero.ecommerce.item;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringCloudApplication
@EnableJdbcRepositories
public class ItemApplication {
	public static void main(String[] args) {
		SpringApplication.run(ItemApplication.class, args);
	}
}
