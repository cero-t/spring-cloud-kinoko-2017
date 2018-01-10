package ninja.cero.ecommerce.payment.client;

import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;

import ninja.cero.ecommerce.payment.domain.Payment;

public class PaymentClient {
	private static final String PAYMENT_URL = "http://payment-service";

	WebClient webClient;

	public PaymentClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public void check(Payment payment) {
		webClient.post().uri(PAYMENT_URL + "/check").syncBody(payment).exchange().block();
	}

	public void processPayment(Payment payment) {
		webClient.post().uri(PAYMENT_URL).syncBody(payment).exchange().block();
	}

	public List<Payment> findAll() {
		return webClient.get().uri(PAYMENT_URL).retrieve().bodyToFlux(Payment.class).collectList().block();
	}
}
