package dev.dixie.controller;

import dev.dixie.model.dto.ImagerPostDTO;
import dev.dixie.service.interfaces.Gateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway")
public class GatewayController {

    private final Gateway gatewayService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadImagerPost(@RequestPart("data") String payloadJson,
                                                   @RequestPart("image") MultipartFile image,
                                                   Authentication authentication) throws IOException {
        return gatewayService.uploadImagerPost(payloadJson, image, authentication);
    }

    @GetMapping("/post")
    public ResponseEntity<ImagerPostDTO> getImagerPost(@RequestParam String id) {
        return gatewayService.getCachedImagerPost(id);
    }


    @GetMapping("/posts")
    public ResponseEntity<List<ImagerPostDTO>> getImagerPostsByEmail(@RequestParam String email) {
        return gatewayService.getCachedImagerPostsByEmail(email);
    }

    @PatchMapping("/edit")
    public ResponseEntity<ImagerPostDTO> editPost(@RequestPart("id") String id,
                                                  @RequestPart(value = "data", required = false) String payloadJson,
                                                  @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return gatewayService.editImagerPost(id, payloadJson, image);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam String id) {
        return gatewayService.deleteImagerPost(id);
    }
}
