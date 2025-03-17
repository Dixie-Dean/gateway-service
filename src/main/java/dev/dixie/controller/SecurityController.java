package dev.dixie.controller;

import dev.dixie.exception.EmailAlreadyTakenException;
import dev.dixie.model.dto.SignInData;
import dev.dixie.model.dto.SignUpData;
import dev.dixie.service.interfaces.Security;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final Security securityService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> register(@RequestBody SignUpData signUpData) throws EmailAlreadyTakenException {
        var response = securityService.signUp(signUpData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody SignInData signInData) {
        var token = securityService.signIn(signInData);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
