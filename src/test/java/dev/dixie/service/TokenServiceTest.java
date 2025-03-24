package dev.dixie.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private JwtEncoder encoder;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void testGenerateToken_SuccessfullyGeneratesToken() {
        var username = "test-user";
        var authorities = List.of(new SimpleGrantedAuthority("USER"));
        var authentication = new TestingAuthenticationToken(username, null, authorities);
        var expectedJwtValue = "mockJwtToken";

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn(expectedJwtValue);

        final JwtClaimsSet[] capturedClaims = new JwtClaimsSet[1];
        when(encoder.encode(any(JwtEncoderParameters.class))).thenAnswer(invocation -> {
            JwtEncoderParameters params = invocation.getArgument(0);
            capturedClaims[0] = params.getClaims();
            return mockJwt;
        });

        var token = tokenService.generateToken(authentication);

        assertNotNull(token);
        assertEquals(expectedJwtValue, token);
        assertEquals(username, capturedClaims[0].getSubject());
        assertNotNull(capturedClaims[0].getIssuedAt());
        assertNotNull(capturedClaims[0].getExpiresAt());
        assertEquals("USER", capturedClaims[0].getClaims().get("authorities"));

        verify(encoder, atLeastOnce()).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void testGenerateToken_HandlesNullAuthentication() {
        assertThrows(NullPointerException.class, () -> tokenService.generateToken(null));
    }
}
