package ninja.cero.ecommerce.store.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ninja.cero.ecommerce.order.client.OrderClient;
import ninja.cero.ecommerce.order.domain.OrderInfo;
import ninja.cero.ecommerce.store.UserContext;

@RestController
@RequestMapping("/order")
public class OrderController {
	@Autowired
	OrderClient orderClient;

	@Autowired
	UserContext userContext;

	@PostMapping
	public void checkout(@RequestBody OrderInfo order) {
		// Check cart
		if (userContext.cartId == null) {
			throw new RuntimeException("No valid cart!");
		}

		// Save order
		order.cartId = userContext.cartId;
		userContext.cartId = null;

		orderClient.createOrder(order);
	}
}
