package com.dainam.D2.models.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("ADMIN:GET"),
    ADMIN_UPDATE("ADMIN:PUT"),
    ADMIN_CREATE("ADMIN:POST"),
    ADMIN_DELETE("ADMIN:DELETE"),
    ENGR_READ("ENGR:GET"),
    ENGR_UPDATE("ENGR:PUT"),
    ENGR_CREATE("ENGR:POST"),
    ENGR_DELETE("ENGR:DELETE"),
    USER_READ("USER:GET"),
    USER_UPDATE("USER:PUT"),
    USER_CREATE("USER:POST"),
    USER_DELETE("USER:DELETE"),

    ANONYMOUS("ANONYMOUS");

    @Getter
    private final String permission;
}
