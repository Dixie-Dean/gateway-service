package dev.dixie.security.authentication.service;

import dev.dixie.security.authentication.model.dto.RegisterData;
import dev.dixie.security.authentication.model.dto.SignInData;

public interface SecurityService {

    String register(RegisterData registerData);

    String signIn(SignInData signInData);
}
