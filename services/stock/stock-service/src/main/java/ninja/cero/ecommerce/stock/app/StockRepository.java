package ninja.cero.ecommerce.stock.app;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import ninja.cero.ecommerce.stock.domain.Stock;

@Component
public interface StockRepository extends CrudRepository<Stock, Long> {
	@Modifying
	@Query("update stock s set s.quantity = s.quantity + :quantity where s.id = :id")
	public Integer add(@Param("id") Long id, @Param("quantity") int quantity);

	@Modifying
	@Query("update stock s set s.quantity = s.quantity - :quantity where s.id = :id and s.quantity > :quantity")
	public Integer subtractIfPossible(@Param("id") Long id, @Param("quantity") int quantity);
}
