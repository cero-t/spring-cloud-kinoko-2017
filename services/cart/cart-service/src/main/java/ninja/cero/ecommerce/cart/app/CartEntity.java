package ninja.cero.ecommerce.cart.app;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class CartEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	String items;
}
