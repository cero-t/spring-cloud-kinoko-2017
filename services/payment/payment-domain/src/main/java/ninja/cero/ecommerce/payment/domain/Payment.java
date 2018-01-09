package ninja.cero.ecommerce.payment.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public class Payment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public String cardNumber;

	public String expire;

	public String name;

	public BigDecimal amount;
}
