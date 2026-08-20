package hei.school.app.controller;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.mapper.UserMapper;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.jwt.JwtService;
import hei.school.app.security.model.Principal;
import hei.school.app.security.model.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

class TranscriptControllerTest extends FacadeIT {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UserRepository userRepository;
    @Autowired private CursusRepository cursusRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private UserMapper userMapper;

    private JUser student;
    private JUser otherStudent;
    private JUser admin;

    @BeforeEach
    void setUp() {
        student = userRepository.save(newUser(UserRole.STUDENT, "student1@hei.school"));
        otherStudent = userRepository.save(newUser(UserRole.STUDENT, "student2@hei.school"));
        admin = userRepository.save(newUser(UserRole.ADMIN, "admin@hei.school"));

        JCursus cursus = cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build()
        );
        JCourse course = courseRepository.save(
            JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build()
        );
        JExam exam = examRepository.save(
            JExam.builder()
                .examDate(Instant.now())
                .coefficient(BigDecimal.ONE)
                .course(course)
                .build()
        );
        gradeRepository.save(
            JGrade.builder().exam(exam).student(student).gradedBy(admin).build()
        );
    }

    @AfterEach
    void tearDown() {
        gradeRepository.deleteAll();
        examRepository.deleteAll();
        courseRepository.deleteAll();
        cursusRepository.deleteAll();
        userRepository.deleteAll();
    }

    private JUser newUser(UserRole role, String email) {
        return JUser.builder()
            .id(UUID.randomUUID())
            .firstName("Test")
            .lastName("User")
            .role(role)
            .email(email)
            .password(passwordEncoder.encode("password123"))
            .build();
    }

    private HttpHeaders authHeaders(JUser jUser) {
        Principal principal = new Principal(userMapper.toModel(jUser));
        String token = jwtService.generate(principal);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void should_accept_when_student_requests_own_transcript() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders(student));

        ResponseEntity<Void> response = restTemplate.exchange(
            "/transcripts/" + student.getId(),
            HttpMethod.POST,
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    void should_forbid_when_student_requests_another_students_transcript() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders(student));

        ResponseEntity<Void> response = restTemplate.exchange(
            "/transcripts/" + otherStudent.getId(),
            HttpMethod.POST,
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void should_accept_when_admin_requests_any_students_transcript() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders(admin));

        ResponseEntity<Void> response = restTemplate.exchange(
            "/transcripts/" + student.getId(),
            HttpMethod.POST,
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    void should_return_not_found_when_student_does_not_exist() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders(admin));
        UUID unknownId = UUID.randomUUID();

        ResponseEntity<Void> response = restTemplate.exchange(
            "/transcripts/" + unknownId,
            HttpMethod.POST,
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void should_return_forbidden_when_no_token_provided() {
        HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
            "/transcripts/" + student.getId(),
            HttpMethod.POST,
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}