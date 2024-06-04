package com.dainam.D2.dto.auth;

import com.dainam.D2.models.auth.Role;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String username;

    private Role role;

    private String accessToken;

    private String refreshToken;

}
