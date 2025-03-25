package dev.dixie.service;

import dev.dixie.exception.EmailAlreadyTakenException;
import dev.dixie.model.dto.SignInData;
import dev.dixie.model.dto.SignUpData;
import dev.dixie.model.entity.ImagerUser;
import dev.dixie.repository.UserRepository;
import dev.dixie.role.Role;
import dev.dixie.service.interfaces.Security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService implements Security {

    private final AuthenticationManager manager;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    public String signUp(SignUpData signUpData) throws EmailAlreadyTakenException {
        if (userRepository.isPresent(signUpData.getEmail())) {
            var message = "User with such email [%s] already exists...".formatted(signUpData.getEmail());
            throw new EmailAlreadyTakenException(message);
        } else {
            log.info("SignUp | SignUpData:{}", signUpData);
            return persistUser(signUpData);
        }
    }

    private String persistUser(SignUpData signUpData) {
        var imagerUser = ImagerUser.builder()
                .email(signUpData.getEmail())
                .firstname(signUpData.getFirstname())
                .lastname(signUpData.getLastname())
                .password(passwordEncoder.encode(signUpData.getPassword()))
                .role(String.valueOf(Role.USER))
                .build();
        log.info("SignUp | ImagerUser:{}", imagerUser);
        userRepository.saveUser(imagerUser);
        return "User [%s] successfully saved!".formatted(signUpData.getEmail());
    }

    @Override
    public String signIn(SignInData signInData) {
        var email = signInData.getEmail();
        var password = signInData.getPassword();
        log.info("SignIn | Email:{}, Password:{}", email, password);
        var authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return tokenService.generateToken(authentication);
    }
}
