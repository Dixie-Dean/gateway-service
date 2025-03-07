package dev.dixie.model;

import dev.dixie.model.entity.ImagerUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public record ImagerUserDetails(ImagerUser imagerUser) implements UserDetails {

    @Override
    public String getUsername() {
        return imagerUser.getEmail();
    }

    @Override
    public String getPassword() {
        return imagerUser.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(imagerUser.getRole().split(", ")).map(SimpleGrantedAuthority::new).toList();
    }
}
