package de.personal.taskservice.config;

import de.personal.common.model.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Date;

@Configuration
public class TestTokenGenerator {
    @Value("${app-key}")
    private String SECRET_KEY;

    public String generateToken(String username, UserRole role) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode the secret key
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(username)
                .claim(UserRole.class.getSimpleName(), role.name()) // add roles claim
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000)) // 10 hrs validity
                .signWith(signingKey, Jwts.SIG.HS256) // Use Key and SignatureAlgorithm
                .compact();
    }
}
