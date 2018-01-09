package ninja.cero.ecommerce.order.app;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import ninja.cero.ecommerce.cart.domain.CartDetail;
import ninja.cero.ecommerce.order.domain.EventType;
import ninja.cero.ecommerce.order.domain.OrderEvent;
import ninja.cero.ecommerce.order.domain.OrderInfo;
import ninja.cero.ecommerce.payment.domain.Payment;
import ninja.cero.ecommerce.stock.domain.Stock;

@RestController
@EnableBinding(OrderSource.class)
public class OrderController {
	private static final String CART_URL = "http://cart-service";
	private static final String STOCK_URL = "http://stock-service";
	private static final String PAYMENT_URL = "http://payment-service";

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderEventRepository orderEventRepository;

	@Autowired
	OrderSource orderSource;

	@Autowired
	public OrderController(OrderSource orderSource) {
		System.out.println(orderSource);
	}

	@PostMapping
	public void createOrder(@RequestBody OrderInfo order) {
		orderRepository.save(order);

		CartDetail cart = restTemplate.getForObject(CART_URL + "/" + order.cartId + "/detail", CartDetail.class);

		// Keep stock
		List<Stock> keepRequests = cart.items.values().stream().map(i -> {
			Stock stock = new Stock();
			stock.itemId = i.itemId;
			stock.quantity = i.quantity;
			return stock;
		}).collect(Collectors.toList());
		restTemplate.postForObject(STOCK_URL, keepRequests, Void.class);

		// Check card
		Payment payment = new Payment();
		payment.name = order.cardName;
		payment.expire = order.cardExpire;
		payment.cardNumber = order.cardNumber;
		payment.amount = cart.amount;
		restTemplate.postForObject(PAYMENT_URL + "/check", payment, Void.class);

		// Start orderEvent
		OrderEvent event = new OrderEvent();
		event.orderId = order.id;
		event.eventType = EventType.START;
		event.eventTime = new Timestamp(System.currentTimeMillis());
		orderEventRepository.save(event);

		// Order
		orderSource.order().send(MessageBuilder.withPayload(order).build());

		// Payment

		// SendMail

	}

	@PostMapping("/{orderId}/event")
	public void createEvent(@RequestBody OrderEvent orderEvent) {
		orderEventRepository.save(orderEvent);
	}

	@GetMapping("/events")
	public Iterable<OrderEvent> getEvents() {
		return orderEventRepository.findAll();

	}
}
