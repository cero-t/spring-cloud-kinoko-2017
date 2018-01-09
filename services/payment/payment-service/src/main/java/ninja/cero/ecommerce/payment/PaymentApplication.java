package ninja.cero.ecommerce.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import ninja.cero.ecommerce.payment.app.PaymentSource;

@SpringCloudApplication
@EnableBinding(PaymentSource.class)
@EnableJdbcRepositories
public class PaymentApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}
}
