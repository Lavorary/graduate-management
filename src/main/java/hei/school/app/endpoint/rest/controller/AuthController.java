package hei.school.app.endpoint.rest.controller;

import hei.school.app.dto.AuthResponse;
import hei.school.app.dto.LoginRequest;
import hei.school.app.dto.RegisterRequest;
import hei.school.app.dto.UserResponse;
import hei.school.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request)); // Returns safe DTO
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    var user = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new UserResponse(
                user.id(), user.email(), user.firstName(), user.lastName(), user.role()));
  }
}
