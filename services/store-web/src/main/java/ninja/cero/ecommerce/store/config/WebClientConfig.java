package ninja.cero.ecommerce.store.config;

import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.cart.client.CartClient;
import ninja.cero.ecommerce.item.client.ItemClient;
import ninja.cero.ecommerce.order.client.OrderClient;
import ninja.cero.ecommerce.stock.client.StockClient;

@Configuration
public class WebClientConfig {
	@Bean
	WebClient webClient(LoadBalancerExchangeFilterFunction lbFunction) {
		return WebClient.builder().filter(lbFunction).build();
	}

	@Bean
	ItemClient itemClient(WebClient webClient) {
		return new ItemClient(webClient);
	}

	@Bean
	StockClient stockClient(WebClient webClient) {
		return new StockClient(webClient);
	}

	@Bean
	CartClient cartClient(WebClient webClient) {
		return new CartClient(webClient);
	}

	@Bean
	OrderClient orderClient(WebClient webClient) {
		return new OrderClient(webClient);
	}
}
