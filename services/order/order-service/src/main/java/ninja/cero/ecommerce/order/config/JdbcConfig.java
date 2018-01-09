package ninja.cero.ecommerce.order.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.SqlGeneratorSource;
import org.springframework.data.jdbc.mapping.model.DefaultNamingStrategy;
import org.springframework.data.jdbc.mapping.model.JdbcMappingContext;
import org.springframework.data.jdbc.mapping.model.JdbcPersistentProperty;
import org.springframework.data.jdbc.mapping.model.NamingStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class JdbcConfig {
	@Bean
	DataAccessStrategy dataAccessStrategy(JdbcMappingContext context, DataSource datasource) {
		return new DefaultDataAccessStrategy(new SqlGeneratorSource(context),
				new NamedParameterJdbcTemplate(datasource), context);
	}

	@Bean
	NamingStrategy namingStrategy() {
		return new DefaultNamingStrategy() {
			@Override
			public String getColumnName(JdbcPersistentProperty property) {
				return camelToSnake(property.getName());
			}

			@Override
			public String getTableName(Class<?> type) {
				return camelToSnake(StringUtils.uncapitalize(type.getSimpleName()));
			}

			public String camelToSnake(String original) {
				char[] chars = original.toCharArray();
				char[] buff = new char[chars.length + 10];

				int j = 0;
				for (int i = 0; i < chars.length; i++) {
					char c = chars[i];
					if (buff[buff.length - 1] != ' ') {
						buff = Arrays.copyOf(buff, buff.length + 10);
					}
					if (Character.isUpperCase(c)) {
						buff[j++] = '_';
						buff[j++] = Character.toLowerCase(c);
					} else {
						buff[j++] = c;
					}
				}
				return new String(buff).trim();
			}
		};
	}
}
