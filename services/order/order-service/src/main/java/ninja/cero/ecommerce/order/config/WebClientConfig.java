package ninja.cero.ecommerce.order.config;

import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.cart.client.CartClient;
import ninja.cero.ecommerce.payment.client.PaymentClient;
import ninja.cero.ecommerce.stock.client.StockClient;

@Configuration
public class WebClientConfig {
	@Bean
	WebClient webClient(LoadBalancerExchangeFilterFunction lbFunction) {
		return WebClient.builder().filter(lbFunction).build();
	}

	@Bean
	CartClient cartClient(WebClient webClient) {
		return new CartClient(webClient);
	}

	@Bean
	StockClient stockClient(WebClient webClient) {
		return new StockClient(webClient);
	}

	@Bean
	PaymentClient paymentClient(WebClient webClient) {
		return new PaymentClient(webClient);
	}
}
