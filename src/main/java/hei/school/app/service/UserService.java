package hei.school.app.service;

import hei.school.app.DTOs.CreateUserDTO;
import hei.school.app.DTOs.UserDTO;
import hei.school.app.mapper.UserMapper;
import hei.school.app.model.User;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserDTO create(CreateUserDTO createUserDTO) {
    if (userRepository.findByEmail(createUserDTO.email()).isPresent()) {
      throw new IllegalArgumentException("An account already exists for this email");
    }
    JUser saved =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName(createUserDTO.firstName())
                .lastName(createUserDTO.lastName())
                .role(createUserDTO.role())
                .email(createUserDTO.email())
                .password(passwordEncoder.encode(createUserDTO.password()))
                .build());
    return toDto(saved);
  }

  public UserDTO getById(UUID id) {
    JUser entity =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("User with id : " + id + "  not found"));
    return toDto(entity);
  }

  public UserDTO getByEmail(String email) {
    JUser entity =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new IllegalArgumentException("User with Id : " + email + " not found"));
    return toDto(entity);
  }

  public List<UserDTO> findByRole(UserRole role) {
    return userRepository.findByRole(role).stream().map(this::toDto).toList();
  }

  private UserDTO toDto(JUser entity) {
    User model = userMapper.toModel(entity);
    return UserDTO.builder()
        .id(model.id())
        .firstName(model.firstName())
        .lastName(model.lastName())
        .role(model.role())
        .email(model.email())
        .build();
  }
}
