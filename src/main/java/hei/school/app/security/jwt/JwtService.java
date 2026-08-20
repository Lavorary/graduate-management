package hei.school.app.security.jwt;

import static java.time.Instant.now;

import hei.school.app.security.model.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;

public class JwtService {
  private final SecretKey key;
  private final long expirationMs;

  public JwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
    if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String generate(Principal principal) {
    var issued = now();
    return Jwts.builder()
        .subject(principal.getUsername())
        .claim("uid", principal.user().id())
        .claim("role", principal.user().role().name())
        .issuedAt(Date.from(issued))
        .expiration(Date.from(issued.plusMillis(expirationMs)))
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return parse(token).getPayload().getSubject();
  }

  public boolean isValid(String token, String username) {
    try {
      var claims = parse(token).getPayload();
      return claims.getSubject().equals(username) && claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  private Jws<Claims> parse(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
  }
}
