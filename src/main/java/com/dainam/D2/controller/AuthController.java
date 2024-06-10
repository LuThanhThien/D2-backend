package com.dainam.D2.controller;

import com.dainam.D2.dto.DtoProvider;
import com.dainam.D2.dto.auth.AuthenticationResponse;
import com.dainam.D2.dto.auth.LoginRequest;
import com.dainam.D2.dto.auth.RegisterRequest;
import com.dainam.D2.dto.user.UserDto;
import com.dainam.D2.models.user.User;
import com.dainam.D2.service.auth.AuthenticationService;
import com.dainam.D2.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
@CrossOrigin("*")
@Slf4j
public class AuthController {
    @Autowired
    private final AuthenticationService authService;

    @GetMapping("/hello-world")
    private String helloWorld() {
        return "Hello World!";
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerJwt(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("Get request with LoginDto: " + loginRequest);
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile() {
        User user = authService.getCurrentUser();
        log.info("Get current user: {}", user.getUsername());
        return ResponseEntity.ok(DtoProvider.build(UserDto.class).map(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout() {
        return ResponseEntity.ok(authService.logout());
    }


}
