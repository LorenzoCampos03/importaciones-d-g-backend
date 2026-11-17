package pe.edu.vallegrande.importaciones_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

     @Value("${jwt.secret}")
     private String secret;
     @Value("${jwt.expiration}")
     private Long expiration;

     @Value("${jwt.refresh-expiration}")
     private Long refreshExpiration;

     private SecretKey getSigningKey() {
          return Keys.hmacShaKeyFor(secret.getBytes());
     }

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
          return Jwts.parserBuilder()
                  .setSigningKey(getSigningKey())
                  .build()
                  .parseClaimsJws(token)
                  .getBody();
     }

     private Boolean isTokenExpired(String token) {
          return extractExpiration(token).before(new Date());
     }

     public String generateToken(UserDetails userDetails) {
          Map<String, Object> claims = new HashMap<>();
          return createToken(claims, userDetails.getUsername());
     }

     public String generateToken(String username, String role, Long userId) {
          Map<String, Object> claims = new HashMap<>();
          claims.put("role", role);
          claims.put("userId", userId);
          return createToken(claims, username);
     }

     private String createToken(Map<String, Object> claims, String subject) {
          return Jwts.builder()
                  .setClaims(claims)
                  .setSubject(subject)
                  .setIssuedAt(new Date(System.currentTimeMillis()))
                  .setExpiration(new Date(System.currentTimeMillis() + expiration))
                  .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                  .compact();
     }

     public Boolean validateToken(String token, UserDetails userDetails) {
          final String username = extractUsername(token);
          return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
     }

     public Boolean validateToken(String token, String username) {
          final String extractedUsername = extractUsername(token);
          return (extractedUsername.equals(username) && !isTokenExpired(token));
     }

     public String extractRole(String token) {
          return extractClaim(token, claims -> claims.get("role", String.class));
     }

     public Long extractUserId(String token) {
          final Claims claims = extractAllClaims(token);
          return claims.get("userId", Long.class);
     }

     // Métodos para Refresh Token
     public String generateRefreshToken(String username) {
          Map<String, Object> claims = new HashMap<>();
          claims.put("type", "refresh");
          return createRefreshToken(claims, username);
     }

     private String createRefreshToken(Map<String, Object> claims, String subject) {
          return Jwts.builder()
                  .setClaims(claims)
                  .setSubject(subject)
                  .setIssuedAt(new Date(System.currentTimeMillis()))
                  .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                  .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                  .compact();
     }

     public Boolean validateRefreshToken(String token, String username) {
          try {
               final String tokenUsername = extractUsername(token);
               final Claims claims = extractAllClaims(token);
               final String tokenType = claims.get("type", String.class);

               return (tokenUsername.equals(username) &&
                       !isTokenExpired(token) &&
                       "refresh".equals(tokenType));
          } catch (Exception e) {
               return false;
          }
     }

     public String refreshAccessToken(String refreshToken, String role, Long userId) {
          if (validateRefreshToken(refreshToken, extractUsername(refreshToken))) {
               String username = extractUsername(refreshToken);
               return generateToken(username, role, userId);
          }
          throw new RuntimeException("Invalid refresh token");
     }
}
