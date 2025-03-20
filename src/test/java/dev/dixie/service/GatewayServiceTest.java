package dev.dixie.service;

import com.google.gson.Gson;
import dev.dixie.model.dto.ImagerPostUploadData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GatewayServiceTest {

    private static final String BASE_URL = "http://localhost:8080/imager";
    private static final String UPLOAD_URL = "/upload";
    private static final String POST_URL = "/post";
    private static final String POSTS_URL = "/posts";
    private static final String EDIT_URL = "/edit";
    private static final String DELETE_URL = "/delete";
//    private static final long TTL = 60;
    private static final String EMAIL = "user@gmail.com";
    private static final String MESSAGE = "message";
    private static final long POST_TTL = 10;
    private static final String PAYLOAD_JSON_WITH_EMAIL = "{\"message\":\"message\",\"ttl\":10,\"email\":\"user@gmail.com\"}";
    private static final String PAYLOAD_JSON_NO_EMAIL = "{\"message\":\"message\",\"ttl\":10}";

    @Mock
    private Gson jsonParser;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GatewayService gatewayService;

    @Test
    void uploadImagerPost_SuccessfulUpload() throws IOException {
        var response = "upload success";

        when(authentication.getName()).thenReturn(EMAIL);

        var mockImage = Mockito.mock(MultipartFile.class);
        when(mockImage.getBytes()).thenReturn("image-data".getBytes());

        var url = BASE_URL + UPLOAD_URL;
        when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(String.class)))
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
        verify(restTemplate, atLeastOnce()).postForEntity(eq(url), any(HttpEntity.class), eq(String.class));
    }


}
