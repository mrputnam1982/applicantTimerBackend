package com.mikep.applicantTimer.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Log4j2
@Service
public class JwtUtil {
    @Value("${spring.data.admin_username}")
    private String usernameAdmin;

    private String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if(userDetails.getUsername().equals(usernameAdmin)) return createAdminToken(claims,
                userDetails.getUsername());
        else return createUserToken(claims, userDetails.getUsername());
    }

    private String createAdminToken(Map<String, Object> claims, String subject) {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(24, ChronoUnit.HOURS);
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
    private String createUserToken(Map<String, Object> claims, String subject) {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(3, ChronoUnit.HOURS);
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {

        final String username = extractUsername(token);
        boolean isExpired = isTokenExpired(token);

        return (username.equals(userDetails.getUsername()) && !isExpired);
    }

    public String getUsernameFromAuthHeader(String authHeader) {
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = extractUsername(jwt);
        }
        return username;


    }
}
