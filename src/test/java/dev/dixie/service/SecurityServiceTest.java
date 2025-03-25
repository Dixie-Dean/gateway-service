package dev.dixie.service;

import dev.dixie.exception.EmailAlreadyTakenException;
import dev.dixie.model.dto.SignInData;
import dev.dixie.model.dto.SignUpData;
import dev.dixie.model.entity.ImagerUser;
import dev.dixie.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SecurityService securityService;

    @Test
    void signUp_Success() throws EmailAlreadyTakenException {
        var signUpData = new SignUpData(
                "firstname", "lastname", "user@gmail.com", "password");
        when(userRepository.isPresent(anyString())).thenReturn(false);

        var response = securityService.signUp(signUpData);

        assertEquals("User [user@gmail.com] successfully saved!", response);
        verify(userRepository, atLeastOnce()).saveUser(any(ImagerUser.class));
    }

    @Test
    void signUp_ThrowsEmailAlreadyTakenException() {
        var signUpData = new SignUpData(
                "firstname", "lastname", "user@gmail.com", "password");
        when(userRepository.isPresent(anyString())).thenReturn(true);

        Exception exception = assertThrows(
                EmailAlreadyTakenException.class,
                () -> securityService.signUp(signUpData));

        assertEquals("User with such email [user@gmail.com] already exists...", exception.getMessage());
        verify(userRepository, never()).saveUser(any(ImagerUser.class));

    }

    @Test
    void signIn_Success() {
        var email = "user@gmail.com";
        var password = "password";
        var signInData = new SignInData(email, password);
        var authentication = new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn("mocked-token");

        String token = securityService.signIn(signInData);

        assertEquals("mocked-token", token);
        verify(authenticationManager, atLeastOnce()).authenticate(authentication);
        verify(tokenService, atLeastOnce()).generateToken(authentication);
    }
}
