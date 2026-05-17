package com.costuras.reportes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaims(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Integer getIdFromToken(String token) {
        return getClaim(token, claims -> claims.get("id", Integer.class));
    }

    public String getRoleFromToken(String token) {
        // MS-Auth guarda el rol como claim en el token
        return getClaim(token, claims -> claims.get("role", String.class));
    }

    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    public AdminPrincipal getPrincipalFromToken(String token) {
        Claims claims = getAllClaims(token);
        return AdminPrincipal.builder()
                .id(claims.get("id", Integer.class))
                .username(claims.getSubject())
                .role(claims.get("role", String.class))
                .build();
    }
}
