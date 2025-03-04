package dev.dixie.model;

import dev.dixie.model.entity.ImagerUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

//@Getter
//@RequiredArgsConstructor
//public class ImagerUserDetails implements UserDetails {
//
//    private final ImagerUser imagerUser;
//
//    @Override
//    public String getUsername() {
//        return imagerUser.getEmail();
//    }
//
//    @Override
//    public String getPassword() {
//        return imagerUser.getPassword();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Arrays.stream(imagerUser.getRole().split(", ")).map(SimpleGrantedAuthority::new).toList();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
