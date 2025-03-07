package dev.dixie.service;

import dev.dixie.model.dto.SignInData;
import dev.dixie.model.dto.SignUpData;

public interface Security {

    String signUp(SignUpData signUpData);

    String signIn(SignInData signInData);
}
