package com.dainam.D2.security.config;

public class SecurityPattern {

    protected static final String[] ADMIN_API = {
            "/api/admin/**",
    };
    protected static final String[] STAFF_API = {
            "/api/engr/**",
    };
    protected static final String[] USER_API = {
            "/api/user/**",
            "/api/item/**"
    };
    protected static final String[] WHITE_LIST_API = {
            "/api/auth/**",
            "/**"
    };
    protected static final String LOG_OUT_URL = "/api/auth/logout";

}
