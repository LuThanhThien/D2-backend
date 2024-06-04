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
@AllArgsConstructor
@SuperBuilder
public class ChangePasswordRequest {

    @NotEmpty(message = "Email is required.")
    @Email(message = "Email is invalid format")
    private String username;

    @NotEmpty(message = "Current password is required.")
    private String currentPassword;

    @NotEmpty(message = "New password is required.")
    private String newPassword;

    @NotEmpty(message = "Confirm password is required.")
    private String confirmPassword;

}
