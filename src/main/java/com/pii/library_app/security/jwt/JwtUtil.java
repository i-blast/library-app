package com.pii.library_app.security.jwt;

import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    /**
     * TODO перенести в env
     */
    private static final String SECRET_KEY = "Y/aQcRmTIvybqtIqEfR4KhpqzlKQit+/Yi6igVW1dLg=";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    private final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

/*    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }*/

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim(
                        "roles",
                        user.getRoles().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList())
                )
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException exc) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
