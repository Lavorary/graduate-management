package hei.school.app.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GlobalExceptionHandlerTest {
  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void shouldHandleAccessDenied() {
    var response = handler.handleAccessDenied(new AccessDeniedException("Access Denied"));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody().message()).isEqualTo("Access Denied");
  }

  @Test
  void shouldHandleNotFound() {
    var response = handler.handleNotFound(new UsernameNotFoundException("User not found"));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().message()).isEqualTo("User not found");
  }

  @Test
  void shouldHandleBadCredentials() {
    var response = handler.handleBadCredentials(new BadCredentialsException("Invalid"));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody().message()).isEqualTo("Invalid email or password");
  }

  @Test
  void shouldHandleGenericException() {
    var response = handler.handleGeneric(new RuntimeException("Unexpected"));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody().message()).isEqualTo("Unexpected error occurred");
  }
}
