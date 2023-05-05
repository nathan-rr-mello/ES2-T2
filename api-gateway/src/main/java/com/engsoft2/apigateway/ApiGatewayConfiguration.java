package com.engsoft2.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

	// @Bean
	// public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
	// 	return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
	// 			.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
	// 			.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
	// 			.build());
	// }

    @Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/currency-exchange/**")
						.filters(f -> f.circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("forward:/fallback")))
						//.rewritePath("/currency-exchange/**", "/backingServiceEndpoint"))
						.uri("lb://currency-exchange"))
				.route(p -> p.path("/currency-conversion/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/currency-conversion-feign/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/circuit-breaker/**") 
						.uri("lb://circuit-breaker"))
				.build();
	}    
}
