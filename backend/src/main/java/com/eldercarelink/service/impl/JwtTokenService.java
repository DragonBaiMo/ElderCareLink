package com.eldercarelink.service.impl;

import com.eldercarelink.entity.User;
import com.eldercarelink.repository.UserRepository;
import com.eldercarelink.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * JWT 实现，便于前后端分离传递登录态。
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {
    private final UserRepository userRepository;

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.ttlMinutes:120}")
    private long ttlMinutes;

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlMinutes * 60)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Optional<User> parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Long userId = Long.valueOf(claims.getSubject());
            return userRepository.findById(userId);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
