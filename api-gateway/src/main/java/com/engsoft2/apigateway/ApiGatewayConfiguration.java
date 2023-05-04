package com.engsoft2.apigateway;

import java.time.Duration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class ApiGatewayConfiguration {

	@Bean
	public CircuitBreaker circuitBreaker(){
		CircuitBreakerConfig config = CircuitBreakerConfig.custom()
										.failureRateThreshold(50)
										.waitDurationInOpenState(Duration.ofSeconds(5))
										.build();
		CircuitBreakerRegistry reg = CircuitBreakerRegistry.of(config);
		return reg.circuitBreaker("myCircuitBreaker");
	}

    @Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/currency-exchange/**")
						//.filters(f -> f.circuitBreaker(c -> c.setName("myCircuitBreaker")))
						.uri("lb://currency-exchange"))
				.route(p -> p.path("/currency-conversion/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/currency-conversion-feign/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/circuit-breaker/**") 
						.uri("lb://circuit-breaker"))
				.build();
	}    

   /*@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/currency-exchange/**")
						.uri("lb://currency-exchange"))
				.route(p -> p.path("/currency-conversion/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/currency-conversion-feign/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/circuit-breaker/**") 
						.uri("lb://circuit-breaker"))
				.build();
	}    */
}
