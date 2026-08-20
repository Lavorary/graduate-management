package hei.school.app.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hei.school.app.mapper.UserMapper;
import hei.school.app.model.User;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.jwt.JwtService;
import hei.school.app.security.model.Principal;
import hei.school.app.security.model.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "jwt.secret=7B5WeRD3StECcfPIKTaQenm2Jmzp9jDg3qm7XDaQSBT",
      "jwt.expiration-ms=3600000"
    })
class SecurityIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtService jwtService;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UserMapper userMapper;

  private String adminToken;
  private String teacherToken;
  private String studentToken;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    // Admin
    JUser adminJpa =
        JUser.builder()
            .id(UUID.randomUUID())
            .email("admin@test.com")
            .password(passwordEncoder.encode("pass"))
            .role(UserRole.ADMIN)
            .firstName("Admin")
            .lastName("User")
            .build();
    adminJpa = userRepository.save(adminJpa);
    User adminDomain = userMapper.toModel(adminJpa);
    adminToken = jwtService.generate(new Principal(adminDomain));

    // Teacher
    JUser teacherJpa =
        JUser.builder()
            .id(UUID.randomUUID())
            .email("teacher@test.com")
            .password(passwordEncoder.encode("pass"))
            .role(UserRole.TEACHER)
            .firstName("Teacher")
            .lastName("User")
            .build();
    teacherJpa = userRepository.save(teacherJpa);
    User teacherDomain = userMapper.toModel(teacherJpa);
    teacherToken = jwtService.generate(new Principal(teacherDomain));

    // Student
    JUser studentJpa =
        JUser.builder()
            .id(UUID.randomUUID())
            .email("student@test.com")
            .password(passwordEncoder.encode("pass"))
            .role(UserRole.STUDENT)
            .firstName("Student")
            .lastName("User")
            .build();
    studentJpa = userRepository.save(studentJpa);
    User studentDomain = userMapper.toModel(studentJpa);
    studentToken = jwtService.generate(new Principal(studentDomain));
  }

  @Test
  void shouldAllowAccessToAuthEndpointWithoutToken() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"student@test.com\", \"password\":\"pass\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldDenyAccessToProtectedEndpointWithoutToken() throws Exception {
    mockMvc.perform(get("/api/courses")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminAccessToAdminEndpoint() throws Exception {
    mockMvc
        .perform(
            post("/api/cursus")
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .content("{\"name\":\"CS\",\"description\":\"Comp Sci\",\"year\":\"2026\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldDenyStudentAccessToAdminEndpoint() throws Exception {
    mockMvc
        .perform(
            post("/api/cursus")
                .header("Authorization", "Bearer " + studentToken)
                .contentType("application/json")
                .content("{\"name\":\"CS\",\"description\":\"Comp Sci\",\"year\":\"2026\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldAllowStudentToAccessOwnMeEndpoint() throws Exception {
    mockMvc
        .perform(get("/api/me").header("Authorization", "Bearer " + studentToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.email").value("student@test.com"));
  }

  @Test
  void shouldDenyStudentAccessToOtherStudentsEndpoint() throws Exception {
    String otherStudentId = UUID.randomUUID().toString();
    mockMvc
        .perform(
            get("/api/students/" + otherStudentId)
                .header("Authorization", "Bearer " + studentToken))
        .andExpect(status().isForbidden());
  }
}
