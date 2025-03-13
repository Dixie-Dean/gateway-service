package dev.dixie.service.interfaces;

import dev.dixie.exception.EmailAlreadyTakenException;
import dev.dixie.model.dto.SignInData;
import dev.dixie.model.dto.SignUpData;

public interface Security {

    String signUp(SignUpData signUpData) throws EmailAlreadyTakenException;

    String signIn(SignInData signInData);
}
