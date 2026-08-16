package com.daya.app.backend.security;

import com.daya.app.backend.entity.AccountStatus;
import com.daya.app.backend.entity.ERole;
import com.daya.app.backend.entity.Role;
import com.daya.app.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;
import java.util.Collection;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = Objects.requireNonNull(user);
    }

    public User getUser() {
        return user;
    }

    public UUID getUserId() {
    return user.getUid();
}

    public String getUid() {
        return user.getUid().toString();
    }

    public String getPrimaryEmail() {
        return user.getPrimaryEmail();
    }

    @Override
public Collection<? extends GrantedAuthority> getAuthorities() {

    return user.getRoles()
            .stream()
            .map(Role::getName)
            .filter(Objects::nonNull)
            .map(ERole::name)
            .map(SimpleGrantedAuthority::new)
            .toList();
}

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /*
     * Spring Security's username is used only as the
     * UserDetails identity. Our actual login identity
     * is primary/alternate email.
     */
    @Override
    public String getUsername() {
        return user.getUid().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountStatus() != AccountStatus.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountStatus() != AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        return user.getAccountStatus() == AccountStatus.ACTIVE;
    }
}