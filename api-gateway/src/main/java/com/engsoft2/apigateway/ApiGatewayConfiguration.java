package com.engsoft2.apigateway;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class ApiGatewayConfiguration {

	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
	 	
		CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
				.failureRateThreshold(50)
				.waitDurationInOpenState(Duration.ofMillis(2000))
				.slowCallDurationThreshold(Duration.ofSeconds(2))
				.permittedNumberOfCallsInHalfOpenState(2)
				.minimumNumberOfCalls(4)
				.slidingWindowType(SlidingWindowType.COUNT_BASED)
				.slidingWindowSize(4)
				.build();

		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(cbConfig)
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
	}

    @Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/currency-exchange/**")
						.filters(f -> f.circuitBreaker(c -> c.setName("myCircuitBreaker")
						.setFallbackUri("forward:/fallback")))
						.uri("lb://currency-exchange"))
				.route(p -> p.path("/currency-conversion/**")
						.filters(f -> f.circuitBreaker(c -> c.setName("myCircuitBreaker")
						.setFallbackUri("forward:/fallback")))
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/currency-conversion-feign/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/circuit-breaker/**") 
						.uri("lb://circuit-breaker"))
				.build();
	}    
}
