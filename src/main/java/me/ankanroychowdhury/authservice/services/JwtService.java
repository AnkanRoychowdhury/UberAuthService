package me.ankanroychowdhury.authservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements CommandLineRunner {

    @Value("${jwt.expiry}")
    private int EXPIRY;
    @Value("${jwt.secret}")
    private String SECRET;

    public String createToken(Map<String, Object> claims, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRY + 1000L);
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .subject(email)
                .signWith(getSignKey())
                .compact();
    }

    public Claims extractAllPayloads(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllPayloads(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, String email) {
        final String userEmailFetchedFromToken = extractEmail(token);
        return userEmailFetchedFromToken.equals(email) && !isTokenExpired(token);
    }

    /**
     * This method checks if the token expiry was before the current time stamp or not ?
     * @param token JWT token
     * @return true if token is expired else false
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
    public Object extractFromPayload(String token, String payloadKey) {
        Claims claim = extractAllPayloads(token);
        return (Object) claim.get(payloadKey);
    }


    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "admin@gmail.com");
        claims.put("password", "admin");
        String token = createToken(claims, "admin@gmail.com");
        System.out.println("Created Token "+ token);
        System.out.println(extractFromPayload(token, "password").toString());
    }
}
