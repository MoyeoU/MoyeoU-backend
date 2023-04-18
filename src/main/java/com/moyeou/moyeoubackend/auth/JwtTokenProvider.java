package com.moyeou.moyeoubackend.auth;

import com.moyeou.moyeoubackend.member.domain.Member;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret-key")
    private String secretKey;

    public String createToken(Member member) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 1000 * 60 * 60);

        return Jwts.builder()
                .setSubject(member.getId().toString())
                .setClaims(Map.of("email", member.getEmail()))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(HS256, secretKey)
                .compact();
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);

        if (header == null || !header.startsWith("BEARER")) {
            return null;
        }

        return header.substring("BEARER ".length());
    }

    public boolean validateToken(String token) {
        Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        Date expiration = Jwts.claims().getExpiration();
        if (expiration.before(new Date())) {
        }
    }
}
