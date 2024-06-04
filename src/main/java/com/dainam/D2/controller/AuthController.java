package com.dainam.D2.controller;

import com.dainam.D2.dto.auth.AuthenticationResponse;
import com.dainam.D2.dto.auth.LoginRequest;
import com.dainam.D2.dto.auth.RegisterRequest;
import com.dainam.D2.service.auth.AuthenticationService;
import com.dainam.D2.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("Get request with LoginDto: " + loginRequest.toString());
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout() {
        return ResponseEntity.ok(authService.logout());
    }

}
