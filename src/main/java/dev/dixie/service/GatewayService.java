package dev.dixie.service;

import com.nimbusds.jose.shaded.gson.Gson;
import dev.dixie.model.dto.ImagerPostDTO;
import dev.dixie.model.dto.ImagerPostUploadData;
import lombok.RequiredArgsConstructor;
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

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GatewayService {

    private final static String BASE_URL = "http://localhost:8080/imager";
    private final static String UPLOAD_URL = "/upload";
    private final static String POST_URL = "/post";
    private final static String POSTS_URL = "/posts";
    private final static String EDIT_URL = "/edit";
    private final static String DELETE_URL = "/delete";
    private final RestTemplate restTemplate;
    private final Gson jsonParser = new Gson();

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

    public ResponseEntity<ImagerPostDTO> getImagerPost(String id) {
        log.info("GetImagerPost | ID:{}", id);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + POST_URL).queryParam("id", id).toUriString();
        log.info("GetImagerPost | URI:{}", uri);
        return restTemplate.getForEntity(uri, ImagerPostDTO.class);
    }

    public ResponseEntity<List<ImagerPostDTO>> getImagerPostsByUsername(String username) {
        log.info("GetImagerPostsByUsername | Username:{}", username);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + POSTS_URL).queryParam("username", username).toUriString();
        log.info("GetImagerPostsByUsername | URI:{}", uri);
        var response = new ParameterizedTypeReference<List<ImagerPostDTO>>() {};
        return restTemplate.exchange(uri, HttpMethod.GET, null, response);
    }

    public ResponseEntity<ImagerPostDTO> editImagerPost(String id, String payloadJson, MultipartFile image) throws IOException {
        log.info("EditImagerPost | ID:{}, JSON:{}, Image:{}", id, payloadJson, image);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + EDIT_URL).toUriString();
        var body = new LinkedMultiValueMap<>();
        body.add("id", id);
        body.add("data", payloadJson);
        body.add("image", convertToResource(image));
        return restTemplate.exchange(uri, HttpMethod.PATCH, new HttpEntity<>(body, setHeaders()), ImagerPostDTO.class);
    }

    public ResponseEntity<String> deleteImagerPost(String id) {
        log.info("DeleteImagerPost | ID:{}", id);
        var uri = UriComponentsBuilder.fromUriString(BASE_URL + DELETE_URL).queryParam("id", id).toUriString();
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);
    }
}
