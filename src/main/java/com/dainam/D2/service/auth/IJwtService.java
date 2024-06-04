package com.dainam.D2.service.auth;

import com.dainam.D2.models.user.User;
import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.function.Function;


interface IJwtService {

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(User user);

    String generateToken(Map<String, Object> extraClaims, User user);

    String generateRefreshToken(User user);

    boolean isValidToken(String token, User user);

}
