package org.safescan.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String KEY = "SAFE_SCAN";

    public static String generateToken(Map<String, Object> claims){
        return JWT.create()
                .withClaim("claims", claims)  // Add claims
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // Expire time（2h）
                .sign(Algorithm.HMAC256(KEY)); // Set keys and algorithm
    }


    public static Map<String, Object> parseToken(String token){
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }
}
