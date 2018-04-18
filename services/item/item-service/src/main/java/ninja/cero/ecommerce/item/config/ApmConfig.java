package ninja.cero.ecommerce.item.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import co.elastic.apm.servlet.ApmFilter;
import co.elastic.apm.spring.webmvc.ApmHandlerInterceptor;

@Configuration
public class ApmConfig implements WebMvcConfigurer {
	@Bean
	public FilterRegistrationBean<ApmFilter> someFilterRegistration() {
		FilterRegistrationBean<ApmFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new ApmFilter());
		registration.addUrlPatterns("/*");
		registration.setName("apmFilter");
		registration.setOrder(1);
		return registration;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ApmHandlerInterceptor());
	}
}