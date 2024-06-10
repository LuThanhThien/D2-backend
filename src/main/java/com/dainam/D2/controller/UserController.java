package com.dainam.D2.controller;

import com.dainam.D2.dto.DtoProvider;
import com.dainam.D2.dto.auth.ChangePasswordRequest;
import com.dainam.D2.dto.user.UserDto;
import com.dainam.D2.models.user.User;
import com.dainam.D2.service.auth.AuthenticationService;
import com.dainam.D2.service.user.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class UserController {

    private final UserService userService;

    private final AuthenticationService authService;

    private final Environment environment;

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

    @PostMapping("/upload-designation")
    public ResponseEntity<UserDto> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("START upload designation");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Upload designation for user {}", username);
        List<String> validContentTypes = Arrays.asList("image/jpeg", "image/png");
        if(!validContentTypes.contains(file.getContentType())){
            throw new RuntimeException("File type is not supported");
        }

        String size = environment.getProperty("spring.servlet.multipart.max-file-size");
        if (size == null) size = "50MB";    // default
        DataSize dataSize = DataSize.parse(size);
        if(file.getSize() > dataSize.toBytes()){
            throw new RuntimeException("File size cannot exceed " + size);
        }

        return ResponseEntity.ok(
                DtoProvider.build(UserDto.class).map(userService.uploadDesignation(file, username))
        );
    }


}
