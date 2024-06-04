package com.dainam.D2.models.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public enum Role {

    ADMIN(
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_CREATE,
                    Permission.ADMIN_DELETE
            ),
            "Admin"
    ),
    ENGR(
            Set.of(
                    Permission.ENGR_READ,
                    Permission.ENGR_UPDATE,
                    Permission.ENGR_CREATE,
                    Permission.ENGR_DELETE
            ),
            "Engineer"
    ),
    USER(
            Set.of(
                    Permission.USER_READ,
                    Permission.USER_UPDATE,
                    Permission.USER_CREATE,
                    Permission.USER_DELETE
            ),
            "User"
    ),

    ANONYMOUS(
            Set.of(
                    Permission.ANONYMOUS
            ),
            "Anonymous"
    ),

    ;

    @Getter
    private final Set<Permission> permissions;

    @Getter
    private final String name;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Permission permission : getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(permission.name()));
        }
        authorities.add(new SimpleGrantedAuthority(
                "ROLE_" + this.name()
        ));
        log.info("This authorities: " + authorities);
        return authorities;
    }


}
