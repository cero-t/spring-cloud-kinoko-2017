package ninja.cero.ecommerce.stock.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class Stock implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	public Long itemId;

	public Integer quantity;
}
