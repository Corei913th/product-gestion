package eadl.productgestion.security;

import eadl.productgestion.entity.User;
import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AuthUser implements UserDetails {
    private final String userPassword;
    private final UserAccountStatus userAccountStatus;
    private final List<SimpleGrantedAuthority> grantedAuthorities;

    public AuthUser (User user){
        Long userId = user.getId();
        String userName = user.getUserName();
        this.userPassword = user.getUserPassword();
        UserRole userRole = user.getUserRole();
        this.userAccountStatus = user.getUserAccountStatus();
        this.grantedAuthorities = userRole.getAuthorities();
    }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableList(grantedAuthorities);
    }

    @Override
    public @Nullable String getPassword() {
        return this.userPassword;
    }

    @Override
    @NullMarked
    public String getUsername() {
        return this.userPassword;
    }


    @Override
    public boolean isEnabled() {
        return this.userAccountStatus == UserAccountStatus.ACTIVE;
    }
}
