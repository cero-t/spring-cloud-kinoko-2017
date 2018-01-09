package ninja.cero.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import ninja.cero.ecommerce.order.app.OrderSource;

@SpringBootApplication
@EnableBinding(OrderSource.class)
@EnableJdbcRepositories
public class OrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}
}
