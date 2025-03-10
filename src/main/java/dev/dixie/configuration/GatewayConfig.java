package dev.dixie.configuration;

import dev.dixie.exception.ResponseErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GatewayConfig {

    @Bean
    public RestTemplate restTemplate() {
        var factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(600_000);
        factory.setReadTimeout(600_000);
        var restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new ResponseErrorHandler());
        return restTemplate;
    }
}
