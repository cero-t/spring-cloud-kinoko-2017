package ninja.cero.ecommerce.order.app;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import ninja.cero.ecommerce.order.domain.OrderInfo;

@Component
public interface OrderRepository extends CrudRepository<OrderInfo, String> {
}
