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

import ninja.cero.ecommerce.cart.client.CartClient;
import ninja.cero.ecommerce.order.domain.EventType;
import ninja.cero.ecommerce.order.domain.OrderEvent;
import ninja.cero.ecommerce.order.domain.OrderInfo;
import ninja.cero.ecommerce.payment.client.PaymentClient;
import ninja.cero.ecommerce.payment.domain.Payment;
import ninja.cero.ecommerce.stock.client.StockClient;
import ninja.cero.ecommerce.stock.domain.Stock;

@RestController
@EnableBinding(OrderSource.class)
public class OrderController {
	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderEventRepository orderEventRepository;

	@Autowired
	OrderSource orderSource;

	@Autowired
	CartClient cartClient;

	@Autowired
	StockClient stockClient;

	@Autowired
	PaymentClient paymentClient;

	@Autowired
	public OrderController(OrderSource orderSource) {
		System.out.println(orderSource);
	}

	@PostMapping
	public void createOrder(@RequestBody OrderInfo order) {
		orderRepository.save(order);

		cartClient.findCartDetailById(order.cartId).subscribe(cart -> {
			if (cart == null) {
				throw new RuntimeException("Cart not found");
			}

			// Keep stock
			List<Stock> keepRequests = cart.items.stream().map(i -> {
				Stock stock = new Stock();
				stock.itemId = i.itemId;
				stock.quantity = i.quantity;
				return stock;
			}).collect(Collectors.toList());
			stockClient.keepStock(keepRequests);

			// Check card
			Payment payment = new Payment();
			payment.name = order.cardName;
			payment.expire = order.cardExpire;
			payment.cardNumber = order.cardNumber;
			payment.amount = cart.total;
			paymentClient.check(payment);

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
		});
	}

	@PostMapping("/{orderId}/event")
	public void createEvent(@RequestBody OrderEvent orderEvent) {
		orderEventRepository.save(orderEvent);
	}

	@GetMapping("/")
	public Iterable<OrderInfo> getOrders() {
		return orderRepository.findAll();
	}

	@GetMapping("/events")
	public Iterable<OrderEvent> getEvents() {
		return orderEventRepository.findAll();

	}
}
