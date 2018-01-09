package ninja.cero.ecommerce.item.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.data.annotation.Id;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public String name;

	public String author;

	public BigDecimal unitPrice;

	public Date release;

	public String image;
}
