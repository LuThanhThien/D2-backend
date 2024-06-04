package com.dainam.D2.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class RegisterRequest {


    @NotEmpty(message = "Email is required.")
    @Email(message = "Email is invalid format.")
    private String username;

    @NotEmpty(message = "Password is required")
    private String password;


}
