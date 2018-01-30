package ninja.cero.ecommerce.cart.config;

import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.item.client.ItemClient;

@Configuration
public class WebClientConfig {
	@Bean
	WebClient webClient(LoadBalancerExchangeFilterFunction lbFunction) {
		// return WebClient.builder().filter(lbFunction).build();
		return WebClient.builder().build();
	}

	@Bean
	ItemClient itemClient(WebClient webClient) {
		return new ItemClient(webClient);
	}
}
