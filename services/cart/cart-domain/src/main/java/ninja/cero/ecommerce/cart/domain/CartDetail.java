package ninja.cero.ecommerce.cart.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;

public class CartDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	public Long cartId;

	public List<CartItem> items;

	public BigDecimal total;
}
