package com.dainam.D2.service.auth;

import com.dainam.D2.dto.auth.AuthenticationResponse;
import com.dainam.D2.dto.auth.ChangePasswordRequest;
import com.dainam.D2.dto.auth.LoginRequest;
import com.dainam.D2.dto.auth.RegisterRequest;
import com.dainam.D2.models.user.User;

public interface IAuthenticationService {

    AuthenticationResponse register(RegisterRequest registerRequest);

    AuthenticationResponse login(LoginRequest loginRequest);

    AuthenticationResponse logout();

    User changePassword(ChangePasswordRequest changePasswordRequest);

    User getCurrentUser();
}
