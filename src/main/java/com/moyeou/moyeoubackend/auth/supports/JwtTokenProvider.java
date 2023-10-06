package com.moyeou.moyeoubackend.auth.supports;

import com.moyeou.moyeoubackend.auth.service.CustomUserDetailsService;
import com.moyeou.moyeoubackend.member.domain.Member;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-key}")
    private String refreshKey;

    public String createAccessToken(Member member) {
        return createToken(member, 1000 * 60 * 60 * 5, secretKey);
    }

    public String createRefreshToken(Member member) {
        return createToken(member, 1000 * 60 * 60 * 24 * 7, refreshKey);
    }

    public String createToken(Member member, int expiration, String encodingKey) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(member.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(HS256, encodingKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String userId = extractMemberId(token, secretKey);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer")) {
            return null;
        }

        return header.substring("Bearer ".length());
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, secretKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    public boolean validateToken(String token, String encodingKey) {
        Date expiration = Jwts.parser()
                .setSigningKey(encodingKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return !expiration.before(new Date());
    }

    public Long extractMemberIdFromRefreshToken(String token) {
        return Long.valueOf(extractMemberId(token, refreshKey));
    }

    private String extractMemberId(String token, String encodingKey) {
        return Jwts.parser()
                .setSigningKey(encodingKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
