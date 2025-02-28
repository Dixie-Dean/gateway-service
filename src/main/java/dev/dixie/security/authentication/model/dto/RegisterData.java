package dev.dixie.security.authentication.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RegisterData {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
