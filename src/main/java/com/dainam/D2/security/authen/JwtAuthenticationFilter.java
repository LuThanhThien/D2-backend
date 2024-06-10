package com.dainam.D2.security.authen;

import com.dainam.D2.models.user.User;
import com.dainam.D2.repository.auth.ITokenRepository;
import com.dainam.D2.repository.user.IUserRepository;
import com.dainam.D2.service.auth.JwtService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final IUserRepository userRepository;

    private final ITokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        try {
            if (request.getServletPath().contains("/api/v1/auth") &&
                !request.getServletPath().endsWith("/auth/profile")
            ) {
                // ignore the endpoint "api/v1/auth"
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = getJwtFromRequest(request);
            final String username;

            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<User> foundUser = this.userRepository.findByUsername(username);
                if (foundUser.isEmpty()) {
                    throw new AuthenticationException();
                }
                User user = foundUser.get();
                var isTokenValid = tokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);
                if (jwtService.isValidToken(jwt, user) && isTokenValid) {
                    log.info("Get user authorities");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
            log.info("Finish filter chain");
        } catch (Exception e) {
            log.info("There is an error during filter chain");
            throw new RuntimeException(e);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return bearerToken.substring(7);
    }
}
