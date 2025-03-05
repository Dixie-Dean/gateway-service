package dev.dixie.controller;

import dev.dixie.model.dto.ImagerPostDTO;
import dev.dixie.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway")
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadImagerPost(@RequestPart("data") String payloadJson,
                                                   @RequestPart("image") MultipartFile image) throws IOException {
        return gatewayService.uploadImagerPost(payloadJson, image);
    }

    @GetMapping("/post")
    public ResponseEntity<ImagerPostDTO> getImagerPost(@RequestParam String id) {
        return gatewayService.getImagerPost(id);
    }


    @GetMapping("/posts")
    public ResponseEntity<List<ImagerPostDTO>> getImagerPostsByUsername(@RequestParam String username) {
        return gatewayService.getImagerPostsByUsername(username);
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
