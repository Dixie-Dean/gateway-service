package dev.dixie.service;

import dev.dixie.model.dto.RegisterData;
import dev.dixie.model.dto.SignInData;

public interface SecurityService {

    String register(RegisterData registerData);

    String signIn(SignInData signInData);
}
