package ninja.cero.ecommerce.order.domain;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;

public class OrderEvent {
	@Id
	public Long id;

	public Long orderId;

	public String eventType;

	public Timestamp eventTime;
}
