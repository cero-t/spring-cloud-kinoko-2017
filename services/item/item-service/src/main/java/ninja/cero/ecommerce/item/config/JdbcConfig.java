package ninja.cero.ecommerce.item.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.SqlGeneratorSource;
import org.springframework.data.jdbc.mapping.model.JdbcMappingContext;

@Configuration
public class JdbcConfig {
	@Bean
	DataAccessStrategy dataAccessStrategy(JdbcMappingContext context, DataSource datasource) {
		return new DefaultDataAccessStrategy(new SqlGeneratorSource(context), context);
	}
}
