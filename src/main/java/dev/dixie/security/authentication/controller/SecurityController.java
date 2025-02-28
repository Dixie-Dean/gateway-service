package dev.dixie.security.authentication.controller;

import dev.dixie.security.authentication.model.dto.RegisterData;
import dev.dixie.security.authentication.model.dto.SignInData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityController {

//    private final SecurityService securityService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(@RequestBody RegisterData registerData) {
//        return securityService.register(registerData);
        return null;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody SignInData signInData) {
//        return securityService.signIn(signInData);
        return null;
    }
}
