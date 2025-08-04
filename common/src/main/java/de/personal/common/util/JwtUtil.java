package de.personal.common.util;

import de.personal.common.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public class JwtUtil {
    private final SecretKey secretKey; // signing key used to generate or parse a token

    public JwtUtil(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decode the secret key
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); // Generate signing key
    }

    // General method to extract any claim
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(secretKey) // The same key used to sign the JWT
                .build() // Finalize the parser
                .parseSignedClaims(token) // Parse and validate the JWT
                .getPayload(); // Extract the body claims

        return claimsResolver.apply(claims); // Apply the resolver to extract the desired claim
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UserRole extractUserRole(String token) {
        return extractClaim(token, claims -> {
            String roleStr = claims.get(UserRole.class.getSimpleName(), String.class);
            return UserRole.valueOf(roleStr);
        });
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}
