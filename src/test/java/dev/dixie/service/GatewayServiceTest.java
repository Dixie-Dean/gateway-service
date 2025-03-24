package dev.dixie.service;

import com.google.gson.Gson;
import dev.dixie.model.dto.ImagerPostDTO;
import dev.dixie.model.dto.ImagerPostUploadData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GatewayServiceTest {

    private static final String EMAIL = "user@gmail.com";
    private static final String PAYLOAD_JSON_NO_EMAIL = "{\"message\":\"message\",\"ttl\":10}";
    private static final String ID = "ABCDEFGH";

    private static final ImagerPostDTO IMAGER_POST_DTO =
            new ImagerPostDTO(
                    ID,
                    "user@gmail.com",
                    "image".getBytes(),
                    "message",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    "link");

    private static final String IMAGER_POST_DTO_JSON = "{"
            + "\"id\":\"ID\","
            + "\"user\":\"user@gmail.com\","
            + "\"image\":\"bytes\","
            + "\"message\":\"message\","
            + "\"creationTime\":\"2025-03-20T15:30:00\","
            + "\"expirationTime\":\"2025-03-20T15:30:00\","
            + "\"link\":\"link\""
            + "}";

    @Mock
    private Gson jsonParser;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Authentication authentication;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private GatewayService gatewayService;

    @Test
    void uploadImagerPost_SuccessfulUpload() throws IOException {
        var response = "upload success";

        when(authentication.getName()).thenReturn(EMAIL);

        var mockImage = Mockito.mock(MultipartFile.class);
        when(mockImage.getBytes()).thenReturn("image-data".getBytes());

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(response));

        var result = gatewayService.uploadImagerPost(PAYLOAD_JSON_NO_EMAIL, mockImage, authentication);

        assertAll("Assert correct response", () -> {
            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(response, result.getBody());
        });

        verify(authentication, atLeastOnce()).getName();
        verify(jsonParser, atLeastOnce()).fromJson(anyString(), eq(ImagerPostUploadData.class));
        verify(jsonParser, atLeastOnce()).toJson(any(ImagerPostUploadData.class));
        verify(restTemplate, atLeastOnce()).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void getCachedImagerPost_CacheHit_ReturnsPostFromCache() {
        var jedis = Mockito.mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.get(anyString())).thenReturn(IMAGER_POST_DTO_JSON);
        when(jsonParser.fromJson(IMAGER_POST_DTO_JSON, ImagerPostDTO.class)).thenReturn(IMAGER_POST_DTO);

        var response = gatewayService.getCachedImagerPost(ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(IMAGER_POST_DTO, response.getBody());

        verify(jedis, atLeastOnce()).get(anyString());
        verify(jsonParser, atLeastOnce()).fromJson(anyString(), eq(ImagerPostDTO.class));
        verify(restTemplate, never()).getForEntity(anyString(), eq(ImagerPostDTO.class));
        verify(jedis).close();
    }

    @Test
    void getCachedImagerPost_CacheMiss_FetchesFromFallback() {
        var jedis = Mockito.mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.get(anyString())).thenReturn(null);

        when(restTemplate.getForEntity(anyString(), eq(ImagerPostDTO.class)))
                .thenReturn(ResponseEntity.ok(IMAGER_POST_DTO));

        var response = gatewayService.getCachedImagerPost(ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(IMAGER_POST_DTO, response.getBody());

        verify(executorService, atLeastOnce()).execute(any(Runnable.class));
        verify(jedis).close();
    }

    @Test
    void getImagerPost_SuccessfullyReturnsPost() {
        when(restTemplate.getForEntity(anyString(), eq(ImagerPostDTO.class)))
                .thenReturn(ResponseEntity.ok(new ImagerPostDTO()));

        var response = gatewayService.getImagerPost(ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(restTemplate, atLeastOnce()).getForEntity(anyString(), eq(ImagerPostDTO.class));
    }

    @Test
    void getCachedImagerPostsByEmail_CacheHit_ReturnsPostsFromCache() {
        var key = "posts:user@gmail.com";
        var parameterizedTypeReference = new ParameterizedTypeReference<List<ImagerPostDTO>>() {};
        var jedis = Mockito.mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.lrange(key, 0, -1)).thenReturn(List.of(IMAGER_POST_DTO_JSON));
        when(jsonParser.fromJson(IMAGER_POST_DTO_JSON, ImagerPostDTO.class)).thenReturn(IMAGER_POST_DTO);

        var response = gatewayService.getCachedImagerPostsByEmail(EMAIL);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(IMAGER_POST_DTO), response.getBody());

        verify(jedis, atLeastOnce()).lrange(key, 0, -1);
        verify(jsonParser, atLeastOnce()).fromJson(anyString(), eq(ImagerPostDTO.class));
        verify(restTemplate, never()).exchange(
                anyString(), eq(HttpMethod.GET), isNull(), eq(parameterizedTypeReference));
        verify(jedis).close();
    }

    @Test
    void getCachedImagerPostsByEmail_CacheMiss_FetchesFromFallback() {
        var key = "posts:user@gmail.com";
        var parameterizedTypeReference = new ParameterizedTypeReference<List<ImagerPostDTO>>() {};
        var jedis = Mockito.mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.lrange(key, 0, -1)).thenReturn(Collections.emptyList());
        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.GET), isNull(), eq(parameterizedTypeReference)))
                .thenReturn(ResponseEntity.ok(List.of(IMAGER_POST_DTO)));

        var response = gatewayService.getCachedImagerPostsByEmail(EMAIL);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(IMAGER_POST_DTO), response.getBody());

        verify(executorService, atLeastOnce()).execute(any(Runnable.class));
        verify(jedis).close();
    }

    @Test
    void getImagerPostsByEmail_SuccessfullyReturnsPosts() {
        var parameterizedTypeReference = new ParameterizedTypeReference<List<ImagerPostDTO>>() {};
        var body = List.of(new ImagerPostDTO(), new ImagerPostDTO());

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(parameterizedTypeReference)))
                .thenReturn(ResponseEntity.ok(body));

        var response = gatewayService.getImagerPostsByEmail(EMAIL);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(restTemplate, atLeastOnce()).exchange(
                anyString(), eq(HttpMethod.GET), isNull(), eq(parameterizedTypeReference));
    }

    @Test
    void editImagerPost_Success() throws IOException {
        when(authentication.getName()).thenReturn(EMAIL);
        var mockImage = Mockito.mock(MultipartFile.class);
        when(mockImage.getBytes()).thenReturn("image-data".getBytes());

        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(ImagerPostDTO.class)))
                .thenReturn(ResponseEntity.ok(IMAGER_POST_DTO));

        var response = gatewayService.editImagerPost(ID, PAYLOAD_JSON_NO_EMAIL, mockImage, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(IMAGER_POST_DTO, response.getBody());
    }

    @Test
    void deleteImagerPost_Success() {
        var responseMessage = "deleted";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), isNull(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseMessage));

        var response = gatewayService.deleteImagerPost(ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseMessage, response.getBody());
    }
}
