package dev.dixie.service;

import dev.dixie.model.dto.RegisterData;
import dev.dixie.model.dto.SignInData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationProvider authenticationProvider;
//    private final JwtManager jwtManager;

    @Override
    public String register(RegisterData registerData) {
        return null;
    }

    @Override
    public String signIn(SignInData signInData) {
//        Authentication authentication = authenticationProvider.authenticate(
//                new UsernamePasswordAuthenticationToken(signInData.getEmail(), signInData.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        PastebinUserDetails principal = (PastebinUserDetails) authentication.getPrincipal();
//        String principalUsername = principal.getPastebinUser().getUsername();
//        String token = jwtManager.generateToken(authentication);

        return null;
    }
}
