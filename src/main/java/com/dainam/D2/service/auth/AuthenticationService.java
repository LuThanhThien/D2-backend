package com.dainam.D2.service.auth;

import com.dainam.D2.dto.auth.AuthenticationResponse;
import com.dainam.D2.dto.auth.ChangePasswordRequest;
import com.dainam.D2.dto.auth.LoginRequest;
import com.dainam.D2.dto.auth.RegisterRequest;
import com.dainam.D2.mapper.auth.AuthenticationMapper;
import com.dainam.D2.mapper.auth.RegisterMapper;
import com.dainam.D2.models.auth.Role;
import com.dainam.D2.models.auth.Token;
import com.dainam.D2.models.auth.TokenType;
import com.dainam.D2.models.user.User;
import com.dainam.D2.repository.auth.ITokenRepository;
import com.dainam.D2.repository.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authManager;

    private final IUserRepository userRepository;

    private final ITokenRepository tokenRepository;


    private User findUserByUsername(String username) {
        Optional<User> foundUser = userRepository.findByUsername(username);
        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException("Username not found!");
        }
        return foundUser.get();
    }

    private void checkExistByUsername(String username) throws AuthenticationException {
        if (userRepository.existsByUsername(username)) {
            throw new AuthenticationException( "Username already exists");
        }
    }

    private void checkUserExist(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException("Username not found!");
        }
    }

    private void authenticate(String username, String password) {
        try {
            // Authenticate whether password correct
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty())
                throw new AuthenticationException("User does not exist");
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password,
                            user.get().getAuthorities()
                    )
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public AuthenticationResponse register(RegisterRequest registerRequest) {
        try {
            this.checkExistByUsername(registerRequest.getUsername());
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User user = RegisterMapper.INSTANCE.toEntity(registerRequest);
        user.setRole(Role.USER);
        userRepository.save(user);

        log.info("Register successfully");
        logUserInfo();

        return AuthenticationMapper.INSTANCE
                .toDto(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        this.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        log.info("Find user by loginDto: " + loginRequest);
        User user = this.findUserByUsername(loginRequest.getUsername());

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        deleteOldUserTokens(user);
        saveUserToken(user, jwtToken);
        user.updateLastLogin();

        log.info("Extracted username: " + jwtService.extractUsername(jwtToken));
        log.info("JWT Token: " + jwtToken);
        log.info("Login successfully");
        logUserInfo();
        AuthenticationResponse response = AuthenticationMapper.INSTANCE
                .toDto(user);
        response.setAccessToken(jwtToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    public AuthenticationResponse logout() {
        return AuthenticationResponse.builder()
                .role(Role.ANONYMOUS)
                .build();
    }

    public User changePassword(ChangePasswordRequest changePasswordRequest) {
        // Validate current password match
        this.authenticate(changePasswordRequest.getUsername(), changePasswordRequest.getCurrentPassword());
        String newPassword = preValidateChain(changePasswordRequest);
        return changePasswordDirect(changePasswordRequest.getUsername(), newPassword);
    }

    private User changePasswordDirect(String username, String newPassword) {
        User user = this.findUserByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.updateLastChangePassword();
        return userRepository.save(user);
    }

    private String preValidateChain(ChangePasswordRequest changePasswordRequest) {
        try {
            String currentPassword = changePasswordRequest.getCurrentPassword();
            String newPassword = changePasswordRequest.getNewPassword();
            String confirmPassword = changePasswordRequest.getConfirmPassword();
            if (Objects.equals(currentPassword, newPassword)) {
                throw new AuthenticationException("Bạn đã nhập lại mật khẩu cũ.");
            }
            if (!Objects.equals(newPassword, confirmPassword)) {
                throw new AuthenticationException("Mật khẩu xác nhận không trùng khớp.");
            }
            return newPassword;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void logUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User user) {
                // Get the principal (User)
                log.info("Username: " + user.getUsername());
            } else {
                // Get the principal (User)
                log.info("Principle: " + authentication.getPrincipal());
            }

            // Get the authorities (roles)
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                log.info("Role: " + authority.getAuthority());
            }

            // Get additional details
            Object details = authentication.getDetails();
            if (details != null) {
                log.info("Details: " + details);
            }

            // Get credentials (password)
            Object credentials = authentication.getCredentials();
            if (credentials != null) {
                log.info("Credentials: " + credentials);
            }
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        user.addToken(token);
        tokenRepository.save(token);
    }

    private List<Token> getRevokeList(User user) {
        var revokedUserTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        log.info("REVOKE LIST FOR " + user.getUsername() + ": \n" + revokedUserTokens);
        return revokedUserTokens;
    }

    private List<Token> getInvalidList(User user) {
        var invalidUserTokens = tokenRepository.findAllInvalidTokenByUserId(user.getId());
        log.info("INVALID LIST FOR " + user.getUsername() + ": \n" + invalidUserTokens);
        return invalidUserTokens;
    }

    private void revokeAllUserTokens(User user) {
        List<Token> revokedUserTokens = getRevokeList(user);
        if (revokedUserTokens.isEmpty()) {
            log.info("List revoked user tokens is empty");
            return;
        }
        this.revokeUsers(revokedUserTokens);
    }

    private void deleteOldUserTokens(User user) {
        List<Token> invalidUserTokens = getInvalidList(user);
        if (invalidUserTokens.isEmpty()) {
            log.info("List invalid user tokens is empty");
            return;
        }
        this.deleteTokens(user, invalidUserTokens);
    }

    private void revokeUsers(List<Token> revokedUserTokens) {
        revokedUserTokens.forEach(token -> {
            log.info("Revoke token with id: " + token.getId());
            token.setExpired(true);
            token.setRevoked(true);
            token.setRevokedDatetime(LocalDateTime.now());
        });
        tokenRepository.saveAll(revokedUserTokens);
    }

    private void deleteTokens(User user, List<Token> invalidTokens) {
        Set<Token> beforeTokens = user.getTokens();
        user.deleteTokens(new HashSet<>(invalidTokens));
        userRepository.save(user);
        Set<Token> afterTokens = user.getTokens();
        tokenRepository.deleteAll(invalidTokens);
        log.info("Number of user tokens before delete: " + beforeTokens.size());
        log.info("Number of user tokens after delete: " + afterTokens.size());
        log.info("Number of deleted tokens (using persist): " + invalidTokens.size());
//        https://stackoverflow.com/questions/22688402/delete-not-working-with-jparepository
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
