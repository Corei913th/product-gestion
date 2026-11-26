package eadl.productgestion.utils;

import eadl.productgestion.security.utils.Permission;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum UserRole {
    ADMIN(EnumSet.allOf(Permission.class)),
    CLIENT(EnumSet.of(Permission.PRODUCT_READ));

    private final Set<Permission> perms;

    UserRole(EnumSet<Permission> permissionsSet) {
        this.perms = permissionsSet;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        this.perms.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.name())));

        return  authorities;
    }

}
