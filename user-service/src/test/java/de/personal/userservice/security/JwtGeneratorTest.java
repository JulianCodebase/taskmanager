package de.personal.userservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtGeneratorTest {
    private final String secretKey = "1i20hBJJioBo===ebw01920hONeoibno1=nOblfnfjk31wnoknjno";
    private JwtGenerator jwtGenerator;

    @BeforeEach
    void setUp() {
        jwtGenerator = new JwtGenerator(secretKey);
    }

    @Test
    void generateToken_shouldIncludeSubject() {
        String token = jwtGenerator.generateToken("user");
        String subject = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        assertEquals("user", subject);
    }
}