package dev.dixie.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SignInData {
    private String email;
    private String password;
}
