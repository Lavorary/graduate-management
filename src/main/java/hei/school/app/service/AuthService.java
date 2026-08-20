package hei.school.app.service;

import hei.school.app.dto.AuthResponse;
import hei.school.app.dto.LoginRequest;
import hei.school.app.dto.RegisterRequest;
import hei.school.app.mapper.UserMapper;
import hei.school.app.model.User;
import hei.school.app.repository.UserRepository;
import hei.school.app.security.jwt.JwtService;
import hei.school.app.security.model.Principal;
import hei.school.app.security.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public AuthResponse login(LoginRequest request) {
    var token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
    var auth = authenticationManager.authenticate(token);
    var principal = (Principal) auth.getPrincipal();
    var user = principal.user();
    var jwt = jwtService.generate(principal);
    return new AuthResponse(jwt, user.id(), user.email(), user.role());
  }

  @Transactional
  public User register(RegisterRequest request) {
    var user =
        User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .role(UserRole.STUDENT)
            .build();
    return userMapper.toModel(userRepository.save(userMapper.toEntity(user)));
  }
}
