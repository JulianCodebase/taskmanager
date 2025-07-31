package de.personal.userservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtGenerator {

    private final String SECRET_KEY; // signing key used to generate or parse a token

    public JwtGenerator(@Value("${app-key}") String secretKey) {
        SECRET_KEY = secretKey;
    }

    public String generateToken(String username) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode the secret key
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000)) // 10 hrs validity
                .signWith(signingKey, Jwts.SIG.HS256) // Use Key and SignatureAlgorithm
                .compact();
    }
}
