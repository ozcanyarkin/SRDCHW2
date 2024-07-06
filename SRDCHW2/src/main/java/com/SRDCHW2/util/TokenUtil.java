package com.SRDCHW2.util;

import com.SRDCHW2.controllers.LoginController;
import io.jsonwebtoken.Jwts;

import java.util.List;

import javax.crypto.SecretKey;

public class TokenUtil {

    private static List<String> tokens;

    private static final SecretKey secretKey = LoginController.secretKey;

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
