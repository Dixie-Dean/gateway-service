package dev.dixie.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.dixie.exception.ImagerPostNotFoundException;
import dev.dixie.model.dto.ImagerPostDTO;
import dev.dixie.model.dto.ImagerPostUploadData;
import dev.dixie.model.dto.adapter.LocalDateTimeAdapter;
import dev.dixie.service.interfaces.Gateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class GatewayService implements Gateway {

    private final static String BASE_URL = "http://localhost:8080/imager";
    private final static String UPLOAD_URL = "/upload";
    private final static String POST_URL = "/post";
    private final static String POSTS_URL = "/posts";
    private final static String EDIT_URL = "/edit";
    private final static String DELETE_URL = "/delete";
    private final static long TTL = 60;

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;
    private final JedisPool jedisPool;
    private final Gson jsonParser;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.executorService = Executors.newFixedThreadPool(10);
        this.jedisPool = new JedisPool("localhost", 6380);
        this.jsonParser = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public ResponseEntity<String> uploadImagerPost(String payloadJson, MultipartFile image, Authentication authentication) throws IOException {
        var email = authentication.getName();
        log.info("UploadImagerPost | JSON:{}, Email:{}, Image:{}", payloadJson, email, image);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + UPLOAD_URL).toUriString();
        var body = new LinkedMultiValueMap<>();
        body.add("data", appendEmail(payloadJson, email));
        body.add("image", convertToResource(image));
        return restTemplate.postForEntity(uri, new HttpEntity<>(body, setHeaders()), String.class);
    }

    private String appendEmail(String json, String email) {
        var imagerPostUploadData = jsonParser.fromJson(json, ImagerPostUploadData.class);
        imagerPostUploadData.setEmail(email);
        return jsonParser.toJson(imagerPostUploadData);
    }

    private HttpHeaders setHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private Resource convertToResource(MultipartFile file) throws IOException {
        if(file == null) {
            return null;
        }

        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }

    @Override
    public ResponseEntity<ImagerPostDTO> getCachedImagerPost(String id) {
        var post = getPostFromCache(id).orElseGet(() -> {
            var imagerPostDTO = Objects.requireNonNull(getImagerPost(id).getBody());
            executorService.execute(() -> pushPostToCache(imagerPostDTO));
            return imagerPostDTO;
        });
        return ResponseEntity.ok(post);
    }

    private Optional<ImagerPostDTO> getPostFromCache(String id) {
        try(var jedis = jedisPool.getResource()) {
            var key = "post:%s".formatted(id);
            var postJson = jedis.get(key);
            if (postJson != null) {
                var imagerPostDTO = jsonParser.fromJson(postJson, ImagerPostDTO.class);
                return Optional.of(imagerPostDTO);
            } else {
                return Optional.empty();
            }
        }
    }

    private void pushPostToCache(ImagerPostDTO post) {
        var id = post.getId();
        try(var jedis = jedisPool.getResource()) {
            var key = "post:%s".formatted(id);
            var postJson = jsonParser.toJson(post);
            jedis.setex(key, TTL, postJson);
        }
    }

    @Override
    public ResponseEntity<ImagerPostDTO> getImagerPost(String id) {
        log.info("GetImagerPost | ID:{}", id);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + POST_URL).queryParam("id", id).toUriString();
        log.info("GetImagerPost | URI:{}", uri);
        return restTemplate.getForEntity(uri, ImagerPostDTO.class);
    }

    @Override
    public ResponseEntity<List<ImagerPostDTO>> getCachedImagerPostsByEmail(String email) {
        var posts = getListFromCache(email).orElseGet(() -> {
            var imagerPostDTOs = Objects.requireNonNull(getImagerPostsByEmail(email).getBody());
            executorService.execute(() -> pushListToCache(imagerPostDTOs));
            return imagerPostDTOs;
        });
        return ResponseEntity.ok(posts);
    }

    private Optional<List<ImagerPostDTO>> getListFromCache(String email) {
        try(var jedis = jedisPool.getResource()) {
            var key = "posts:%s".formatted(email);
            var postsJson = jedis.lrange(key, 0, -1);
            if (!postsJson.isEmpty()) {
                var posts = postsJson.stream().map(json -> jsonParser.fromJson(json, ImagerPostDTO.class)).toList();
                return Optional.of(posts);
            } else {
                return Optional.empty();
            }
        }
    }

    private void pushListToCache(List<ImagerPostDTO> posts) {
        var email = posts.stream().findFirst().map(ImagerPostDTO::getUser).get();
        try(var pipeline = jedisPool.getResource().pipelined()) {
            var key = "posts:%s".formatted(email);
            for (var post : posts) {
                pipeline.lpush(key, jsonParser.toJson(post));
            }
            pipeline.sync();
        }
    }

    @Override
    public ResponseEntity<List<ImagerPostDTO>> getImagerPostsByEmail(String email) {
        log.info("GetImagerPostsByEmail | Email:{}", email);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + POSTS_URL).queryParam("email", email).toUriString();
        log.info("GetImagerPostsByEmail | URI:{}", uri);
        var response = new ParameterizedTypeReference<List<ImagerPostDTO>>() {};
        return restTemplate.exchange(uri, HttpMethod.GET, null, response);
    }

    @Override
    public ResponseEntity<ImagerPostDTO> editImagerPost(String id, String payloadJson, MultipartFile image) throws IOException {
        log.info("EditImagerPost | ID:{}, JSON:{}, Image:{}", id, payloadJson, image);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + EDIT_URL).toUriString();
        var body = new LinkedMultiValueMap<>();
        body.add("id", id);
        body.add("data", payloadJson);
        body.add("image", convertToResource(image));
        return restTemplate.exchange(uri, HttpMethod.PATCH, new HttpEntity<>(body, setHeaders()), ImagerPostDTO.class);
    }

    @Override
    public ResponseEntity<String> deleteImagerPost(String id) {
        log.info("DeleteImagerPost | ID:{}", id);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + DELETE_URL).queryParam("id", id).toUriString();
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);
    }
}
