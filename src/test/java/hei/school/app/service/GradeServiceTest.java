package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.GradeDTO;
import hei.school.app.mapper.GradeMapper;
import hei.school.app.model.Course;
import hei.school.app.model.Cursus;
import hei.school.app.model.Exam;
import hei.school.app.model.Grade;
import hei.school.app.model.User;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

  @Mock private GradeRepository gradeRepository;
  @Mock private ExamRepository examRepository;
  @Mock private UserRepository userRepository;
  @Mock private GradeMapper gradeMapper;
  @InjectMocks private GradeService gradeService;

  private JExam jExam(UUID id) {
    return JExam.builder()
        .id(id)
        .examDate(Instant.now())
        .coefficient(BigDecimal.valueOf(1.0))
        .build();
  }

  private JUser jUser(UUID id, String firstName, String lastName, UserRole role) {
    return JUser.builder()
        .id(id)
        .firstName(firstName)
        .lastName(lastName)
        .role(role)
        .email(firstName.toLowerCase() + "@hei.school")
        .password("encoded")
        .build();
  }

  private Exam examModel(UUID id) {
    return new Exam(
        id,
        Instant.now(),
        BigDecimal.valueOf(1.0),
        new Course(
            UUID.randomUUID(),
            new Cursus(UUID.randomUUID(), "CS", "desc", "2024"),
            "REF",
            "Title",
            5,
            null));
  }

  private User userModel(UUID id, String firstName, String lastName, UserRole role) {
    return new User(
        id, firstName, lastName, role, firstName.toLowerCase() + "@hei.school", "encoded");
  }

  @Test
  void should_create_grade() {
    UUID examId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID graderId = UUID.randomUUID();
    UUID gradeId = UUID.randomUUID();

    JExam jExam = jExam(examId);
    JUser jStudent = jUser(studentId, "Student", "One", UserRole.STUDENT);
    JUser jGrader = jUser(graderId, "Teacher", "One", UserRole.TEACHER);

    JGrade savedEntity =
        JGrade.builder().id(gradeId).exam(jExam).student(jStudent).gradedBy(jGrader).build();

    Exam exam = examModel(examId);
    User student = userModel(studentId, "Student", "One", UserRole.STUDENT);
    User grader = userModel(graderId, "Teacher", "One", UserRole.TEACHER);
    Grade model = new Grade(gradeId, exam, student, grader);

    when(examRepository.findById(examId)).thenReturn(Optional.of(jExam));
    when(userRepository.findById(studentId)).thenReturn(Optional.of(jStudent));
    when(userRepository.findById(graderId)).thenReturn(Optional.of(jGrader));
    when(gradeRepository.save(any(JGrade.class))).thenReturn(savedEntity);
    when(gradeMapper.toModel(savedEntity)).thenReturn(model);

    GradeDTO result = gradeService.create(examId, studentId, graderId);

    ArgumentCaptor<JGrade> captor = ArgumentCaptor.forClass(JGrade.class);
    verify(gradeRepository).save(captor.capture());
    assertThat(captor.getValue().getExam()).isEqualTo(jExam);
    assertThat(captor.getValue().getStudent()).isEqualTo(jStudent);
    assertThat(captor.getValue().getGradedBy()).isEqualTo(jGrader);

    assertThat(result.examId()).isEqualTo(examId);
    assertThat(result.studentId()).isEqualTo(studentId);
    assertThat(result.gradedById()).isEqualTo(graderId);
  }

  @Test
  void should_throw_when_exam_not_found_on_create() {
    UUID examId = UUID.randomUUID();
    when(examRepository.findById(examId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> gradeService.create(examId, UUID.randomUUID(), UUID.randomUUID()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(examId.toString());
  }

  @Test
  void should_throw_when_student_not_found_on_create() {
    UUID examId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();

    when(examRepository.findById(examId)).thenReturn(Optional.of(jExam(examId)));
    when(userRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> gradeService.create(examId, studentId, UUID.randomUUID()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(studentId.toString());
  }

  @Test
  void should_throw_when_grader_not_found_on_create() {
    UUID examId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID graderId = UUID.randomUUID();

    when(examRepository.findById(examId)).thenReturn(Optional.of(jExam(examId)));
    when(userRepository.findById(studentId))
        .thenReturn(Optional.of(jUser(studentId, "Student", "One", UserRole.STUDENT)));
    when(userRepository.findById(graderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> gradeService.create(examId, studentId, graderId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(graderId.toString());
  }

  @Test
  void should_get_grade_by_id() {
    UUID gradeId = UUID.randomUUID();
    UUID examId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID graderId = UUID.randomUUID();

    JExam jExam = jExam(examId);
    JUser jStudent = jUser(studentId, "Student", "One", UserRole.STUDENT);
    JUser jGrader = jUser(graderId, "Teacher", "One", UserRole.TEACHER);
    JGrade entity =
        JGrade.builder().id(gradeId).exam(jExam).student(jStudent).gradedBy(jGrader).build();

    Exam exam = examModel(examId);
    User student = userModel(studentId, "Student", "One", UserRole.STUDENT);
    User grader = userModel(graderId, "Teacher", "One", UserRole.TEACHER);
    Grade model = new Grade(gradeId, exam, student, grader);

    when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(entity));
    when(gradeMapper.toModel(entity)).thenReturn(model);

    GradeDTO result = gradeService.getById(gradeId);
    assertThat(result.id()).isEqualTo(gradeId);
    assertThat(result.examId()).isEqualTo(examId);
    assertThat(result.studentId()).isEqualTo(studentId);
    assertThat(result.gradedById()).isEqualTo(graderId);
  }

  @Test
  void should_throw_when_grade_not_found_by_id() {
    UUID gradeId = UUID.randomUUID();
    when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> gradeService.getById(gradeId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(gradeId.toString());
  }

  @Test
  void should_find_by_exam() {
    UUID examId = UUID.randomUUID();
    UUID gradeId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID graderId = UUID.randomUUID();

    JExam jExam = jExam(examId);
    JUser jStudent = jUser(studentId, "Student", "One", UserRole.STUDENT);
    JUser jGrader = jUser(graderId, "Teacher", "One", UserRole.TEACHER);
    JGrade entity =
        JGrade.builder().id(gradeId).exam(jExam).student(jStudent).gradedBy(jGrader).build();

    Exam exam = examModel(examId);
    User student = userModel(studentId, "Student", "One", UserRole.STUDENT);
    User grader = userModel(graderId, "Teacher", "One", UserRole.TEACHER);
    Grade model = new Grade(gradeId, exam, student, grader);

    when(gradeRepository.findByExam_Id(examId)).thenReturn(List.of(entity));
    when(gradeMapper.toModel(entity)).thenReturn(model);

    List<GradeDTO> results = gradeService.findByExam(examId);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().examId()).isEqualTo(examId);
  }

  @Test
  void should_return_empty_list_when_no_grades_found_by_exam() {
    UUID examId = UUID.randomUUID();
    when(gradeRepository.findByExam_Id(examId)).thenReturn(List.of());

    assertThat(gradeService.findByExam(examId)).isEmpty();
  }

  @Test
  void should_find_by_student() {
    UUID studentId = UUID.randomUUID();
    UUID gradeId = UUID.randomUUID();
    UUID examId = UUID.randomUUID();
    UUID graderId = UUID.randomUUID();

    JExam jExam = jExam(examId);
    JUser jStudent = jUser(studentId, "Student", "One", UserRole.STUDENT);
    JUser jGrader = jUser(graderId, "Teacher", "One", UserRole.TEACHER);
    JGrade entity =
        JGrade.builder().id(gradeId).exam(jExam).student(jStudent).gradedBy(jGrader).build();

    Exam exam = examModel(examId);
    User student = userModel(studentId, "Student", "One", UserRole.STUDENT);
    User grader = userModel(graderId, "Teacher", "One", UserRole.TEACHER);
    Grade model = new Grade(gradeId, exam, student, grader);

    when(gradeRepository.findByStudent_Id(studentId)).thenReturn(List.of(entity));
    when(gradeMapper.toModel(entity)).thenReturn(model);

    List<GradeDTO> results = gradeService.findByStudent(studentId);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().studentId()).isEqualTo(studentId);
  }

  @Test
  void should_return_empty_list_when_no_grades_found_by_student() {
    UUID studentId = UUID.randomUUID();
    when(gradeRepository.findByStudent_Id(studentId)).thenReturn(List.of());

    assertThat(gradeService.findByStudent(studentId)).isEmpty();
  }

  @Test
  void should_find_by_teacher() {
    UUID teacherId = UUID.randomUUID();
    UUID gradeId = UUID.randomUUID();
    UUID examId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();

    JExam jExam = jExam(examId);
    JUser jStudent = jUser(studentId, "Student", "One", UserRole.STUDENT);
    JUser jGrader = jUser(teacherId, "Teacher", "One", UserRole.TEACHER);
    JGrade entity =
        JGrade.builder().id(gradeId).exam(jExam).student(jStudent).gradedBy(jGrader).build();

    Exam exam = examModel(examId);
    User student = userModel(studentId, "Student", "One", UserRole.STUDENT);
    User grader = userModel(teacherId, "Teacher", "One", UserRole.TEACHER);
    Grade model = new Grade(gradeId, exam, student, grader);

    when(gradeRepository.findByGradedBy_Id(teacherId)).thenReturn(List.of(entity));
    when(gradeMapper.toModel(entity)).thenReturn(model);

    List<GradeDTO> results = gradeService.findByTeacher(teacherId);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().gradedById()).isEqualTo(teacherId);
  }

  @Test
  void should_return_empty_list_when_no_grades_found_by_teacher() {
    UUID teacherId = UUID.randomUUID();
    when(gradeRepository.findByGradedBy_Id(teacherId)).thenReturn(List.of());

    assertThat(gradeService.findByTeacher(teacherId)).isEmpty();
  }
}
