package ninja.cero.ecommerce.payment.client;

import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.payment.domain.Payment;
import reactor.core.publisher.Mono;

public class PaymentClient {
	private static final String PAYMENT_URL = "http://payment-service";

	WebClient webClient;

	public PaymentClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<Void> check(Payment payment) {
		return webClient.post().uri(PAYMENT_URL + "/check").syncBody(payment).retrieve().bodyToMono(Void.class);
	}

	public Mono<Void> processPayment(Payment payment) {
		return webClient.post().uri(PAYMENT_URL).syncBody(payment).retrieve().bodyToMono(Void.class);
	}

	public Mono<List<Payment>> findAll() {
		return webClient.get().uri(PAYMENT_URL).retrieve().bodyToFlux(Payment.class).collectList();
	}
}