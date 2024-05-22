package com.nikolagrujic.tradingsimulator.util;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    public Claims extractAllClaims(String jwt) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
    }

    public String extractEmail(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    public boolean isJwtExpired(String jwt) {
        return extractAllClaims(jwt).getExpiration().before(new Date());
    }

    public String generateJwt(String email) {
        Map<String,Object> claims = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, Constants.JWT_EXPIRATION_DAYS);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(calendar.getTime())
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public boolean isValidJwt(String jwt, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractEmail(jwt)) && !isJwtExpired(jwt);
    }
}