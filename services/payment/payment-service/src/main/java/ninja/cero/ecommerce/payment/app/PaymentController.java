package ninja.cero.ecommerce.payment.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ninja.cero.ecommerce.order.domain.OrderInfo;
import ninja.cero.ecommerce.payment.domain.Payment;

@RestController
public class PaymentController {
	@Autowired
	PaymentRepository paymentRepository;

	@StreamListener("order")
	public void hoge(OrderInfo order) throws InterruptedException {
		Thread.sleep(1000L);
	}

	@PostMapping("/check")
	public void check(@RequestBody Payment payment) {
		// Do nothing.
	}

	@PostMapping("/payment")
	public void payment(@RequestBody Payment payment) {
		paymentRepository.save(payment);
	}

	@GetMapping("/payment")
	public Iterable<Payment> payments() {
		return paymentRepository.findAll();
	}
}
