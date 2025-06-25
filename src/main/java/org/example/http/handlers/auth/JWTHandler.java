package org.example.http.handlers.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTHandler {
    public JWTHandler() {
    }

    public static String createToken(String login) {
        Algorithm algorithm = Algorithm.HMAC256("qwerty");
        return JWT.create().withIssuer("auth0").withSubject(login).sign(algorithm);
    }

    public static String decode(String token) {
        Algorithm algorithm = Algorithm.HMAC256("qwerty");
        return JWT.require(algorithm).withIssuer("auth0").build().verify(token).getSubject();
    }
}