package com.inventory.app.server.service.authorization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    private static final String SECRET = "${SECRET_KEY}";

    /**
     * Extracts the subject from the token's claim.
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token's claims. It's used to determine whether the token has expired or not.
     * @param token
     * @return
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * A generic method used to extract a specific claim from the JWT token's claims. The function specifies how to extract the desired claim.
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * This method parses the JWT token and returns all of its claims. The 'Jwts' builder is used to create the parser, which is configured with the signing key,
     * and then extract's the token's claims.
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * This method checks whether a JWT token has expired by comparing the token's expiration date to the current date. If expired, return true otherwise false.
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * This method is used to validate a JWT token. It extracts the username from the token and then checks if it matches the username of the provided UserDetails object and it verifies
     * if the token has expired. If token is valid, return true, otherwise false.
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * This method generates a JWT Token. It creates a set of claims (ie subject, issued-at, expiration), and then builds a JWT token using the claims and the signing key.
     * @param username
     * @return
     */
    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * This method is responsible for creating the JWT token. It uses the 'Jwts' builder to specify the claims, subject, issue date, expiration date, and the signing key. The
     * token is then signing and compacted to produce the final JWT token.
     * @param claims
     * @param username
     * @return
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*1))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * This method is used to get the singing key for JWT token creation and validation. It decodes the 'SECRET' key, which is typically a Base64-encoded key, and
     * converts it into a cryptographic key using the 'Keys.hmacShaKeyFor' method.
     * @return
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
