package com.dainam.D2.controller;

import com.dainam.D2.dto.DtoProvider;
import com.dainam.D2.dto.auth.ChangePasswordRequest;
import com.dainam.D2.dto.user.UserDto;
import com.dainam.D2.models.user.User;
import com.dainam.D2.service.auth.AuthenticationService;
import com.dainam.D2.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class UserController {

    private final UserService userService;

    private final AuthenticationService authService;

    @GetMapping("/hello-world")
    private Object helloWorld() {
        return "Hello world";
    }

    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser() {
        log.info("User service: " + userService);
        log.info("AuthService: " + authService);
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(DtoProvider.build(UserDto.class).map(user));
    }

    @PutMapping
    @PreAuthorize("#request.username == principal.name")
    public ResponseEntity<UserDto> updateUser(@P("request") @RequestBody @Valid UserDto userDto) {
        log.info("Update user: ");
        log.info(userDto.toString());
        User user = userService.updateUserInformation(userDto);
        return ResponseEntity.ok(DtoProvider.build(UserDto.class).map(user));
    }

    @PutMapping("/password")
    @PreAuthorize("#request.username == principal.name")
    public ResponseEntity<UserDto> changePassword(@P("request") @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        User user = authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(DtoProvider.build(UserDto.class).map(user));
    }
}
