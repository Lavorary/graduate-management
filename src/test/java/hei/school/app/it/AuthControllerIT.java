package hei.school.app.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hei.school.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class AuthControllerIT {
  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void cleanUp() {
    userRepository.deleteAll();
  }

  @Test
  void shouldRegisterUser() throws Exception {
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"email\":\"new@test.com\",\"password\":\"pass123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("new@test.com"))
        .andExpect(jsonPath("$.role").value("STUDENT"))
        .andExpect(jsonPath("$.password").doesNotExist()); // Ensure password is not returned
  }

  @Test
  void shouldLoginSuccessfully() throws Exception {
    // First register
    mockMvc.perform(
        post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{\"email\":\"login@test.com\",\"password\":\"pass123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"));

    // Then login
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"login@test.com\",\"password\":\"pass123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.email").value("login@test.com"))
        .andExpect(jsonPath("$.role").value("STUDENT"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void shouldFailLoginWithWrongPassword() throws Exception {
    mockMvc.perform(
        post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{\"email\":\"fail@test.com\",\"password\":\"pass123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"));

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"fail@test.com\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldFailRegistrationWithInvalidEmail() throws Exception {
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"email\":\"not-an-email\",\"password\":\"pass123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
        .andExpect(status().isBadRequest());
  }
}
