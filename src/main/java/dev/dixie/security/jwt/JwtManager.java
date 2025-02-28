package dev.dixie.security.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

//@Component
//@RequiredArgsConstructor
//public class JwtManager {
//
//    private final JwtEncoder jwtEncoder;
//
//    public String generateToken(Authentication authentication) {
//        Instant now = Instant.now();
//        long expiresIn = 24;
//
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority).toString();
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(now)
//                .expiresAt(now.plus(expiresIn, ChronoUnit.HOURS))
//                .subject(authentication.getName())
//                .claim("authorities", authorities)
//                .build();
//
//        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//    }
//}
