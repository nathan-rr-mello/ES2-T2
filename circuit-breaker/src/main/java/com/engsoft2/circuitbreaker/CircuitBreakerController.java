package com.engsoft2.circuitbreaker;

import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
public class CircuitBreakerController {
    
    @GetMapping("circuit-breaker/from/{from}/to/{to}")
    @CircuitBreaker(name = "exchange-circuit-breaker", fallbackMethod = "fallback")
    public CurrencyExchangeResponse tryMakeRequest(@PathVariable String from, @PathVariable String to) {
        Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from",from);
		uriVariables.put("to",to);
        ResponseEntity<CurrencyExchangeResponse> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyExchangeResponse.class, uriVariables);
        CurrencyExchangeResponse currencyResponse = responseEntity.getBody();
        return currencyResponse;
    }

    public String fallback() {
        return "THIS IS A FALLBACK";
    }

    @PostMapping("circuit-breaker")
    public String test() {
        return "It's working!!!!";
    }
}
