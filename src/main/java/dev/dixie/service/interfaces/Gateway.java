package dev.dixie.service.interfaces;

import dev.dixie.model.dto.ImagerPostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface Gateway {

    ResponseEntity<String> uploadImagerPost(String payloadJson, MultipartFile image, Authentication authentication) throws IOException;

    ResponseEntity<ImagerPostDTO> getCachedImagerPost(String id);

    ResponseEntity<ImagerPostDTO> getImagerPost(String id);

    ResponseEntity<List<ImagerPostDTO>> getCachedImagerPostsByEmail(String email);

    ResponseEntity<List<ImagerPostDTO>> getImagerPostsByEmail(String email);

    ResponseEntity<ImagerPostDTO> editImagerPost(String id, String payloadJson, MultipartFile image) throws IOException;

    ResponseEntity<String> deleteImagerPost(String id);
}
