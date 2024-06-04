package com.dainam.D2.security.config;

import com.dainam.D2.security.authen.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static com.dainam.D2.models.auth.Role.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityFilterChainConfig extends SecurityPattern {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers(ADMIN_API).hasAnyRole(ADMIN.name())
                                .requestMatchers(STAFF_API).hasAnyRole(
                                        ADMIN.name(),
                                        ENGR.name()
                                )
                                .requestMatchers(USER_API).hasAnyRole(
                                        ADMIN.name(),
                                        ENGR.name(),
                                        USER.name()
                                )
                                .requestMatchers(WHITE_LIST_API).permitAll()
                                .anyRequest().fullyAuthenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl(LOG_OUT_URL)
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request,
                                               response,
                                               authentication)
                                -> SecurityContextHolder.clearContext())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/404")
                )
        ;
        return http.build();
    }

//    https://www.baeldung.com/java-spring-fix-403-error

}
