package ninja.cero.ecommerce.store.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ninja.cero.ecommerce.order.client.OrderClient;
import ninja.cero.ecommerce.order.domain.OrderInfo;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {
	@Autowired
	OrderClient orderClient;

	@PostMapping
	public void checkout(@RequestBody OrderInfo order) {
		orderClient.createOrder(order);
	}
}
