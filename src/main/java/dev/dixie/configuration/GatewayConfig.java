package dev.dixie.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.dixie.exception.ResponseErrorHandler;
import dev.dixie.model.dto.adapter.LocalDateTimeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Bean
    public Gson gson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    }

    @Bean
    public JedisPool jedisPool() {
        var jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setJmxEnabled(false);
        return new JedisPool(jedisPoolConfig, "localhost", 6380);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }
}
