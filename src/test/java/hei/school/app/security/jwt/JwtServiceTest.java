package hei.school.app.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.model.User;
import hei.school.app.security.model.Principal;
import hei.school.app.security.model.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
  private static final String SECRET = "7B5WeRD3StECcfPIKTaQenm2Jmzp9jDg3qm7XDaQSBT";
  private static final long EXPIRATION_MS = 3600000;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService(SECRET, EXPIRATION_MS);
  }

  @Test
  void shouldGenerateAndParseToken() {
    // Given
    var user =
        User.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .role(UserRole.STUDENT)
            .build();
    var principal = new Principal(user);

    // When
    String token = jwtService.generate(principal);
    String username = jwtService.extractUsername(token);
    boolean isValid = jwtService.isValid(token, "test@example.com");

    // Then
    assertThat(token).isNotBlank();
    assertThat(username).isEqualTo("test@example.com");
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldExtractUsernameFromToken() {
    var user =
        User.builder().id(UUID.randomUUID()).email("john@doe.com").role(UserRole.TEACHER).build();
    String token = jwtService.generate(new Principal(user));

    String extracted = jwtService.extractUsername(token);

    assertThat(extracted).isEqualTo("john@doe.com");
  }

  @Test
  void shouldReturnFalseWhenTokenIsExpired() {
    // Create a token that is already expired
    var key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    String expiredToken =
        Jwts.builder()
            .subject("expired@user.com")
            .issuedAt(Date.from(Instant.now().minusSeconds(3600)))
            .expiration(Date.from(Instant.now().minusSeconds(1)))
            .signWith(key)
            .compact();

    boolean isValid = jwtService.isValid(expiredToken, "expired@user.com");

    assertThat(isValid).isFalse();
  }

  @Test
  void shouldReturnFalseWhenTokenIsMalformed() {
    boolean isValid = jwtService.isValid("malformed-token", "user@test.com");
    assertThat(isValid).isFalse();
  }

  @Test
  void shouldReturnFalseWhenSubjectDoesNotMatch() {
    var user =
        User.builder().id(UUID.randomUUID()).email("alice@test.com").role(UserRole.STUDENT).build();
    String token = jwtService.generate(new Principal(user));

    boolean isValid = jwtService.isValid(token, "bob@test.com");

    assertThat(isValid).isFalse();
  }
}
