package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.ExamDTO;
import hei.school.app.mapper.ExamMapper;
import hei.school.app.model.Course;
import hei.school.app.model.Cursus;
import hei.school.app.model.Exam;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
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
class ExamServiceTest {

  @Mock private ExamRepository examRepository;
  @Mock private CourseRepository courseRepository;
  @Mock private ExamMapper examMapper;
  @InjectMocks private ExamService examService;

  private JCourse jCourse(UUID id) {
    return JCourse.builder()
        .id(id)
        .cursus(
            JCursus.builder().id(UUID.randomUUID()).name("DevLog").description("d").year("2026").build())
        .ref("ALG101")
        .title("Algorithmique")
        .credit(5)
        .build();
  }

  private Course courseModel(UUID id) {
    return new Course(
        id, new Cursus(UUID.randomUUID(), "DevLog", "d", "2026"), "ALG101", "Algorithmique", 5, null);
  }

  @Test
  void should_create_exam() {
    UUID courseId = UUID.randomUUID();
    UUID examId = UUID.randomUUID();
    Instant examDate = Instant.parse("2026-06-15T09:00:00Z");
    BigDecimal coefficient = new BigDecimal("2.5");

    JCourse jCourse = jCourse(courseId);
    JExam savedEntity =
        JExam.builder().id(examId).examDate(examDate).coefficient(coefficient).course(jCourse).build();
    Exam model = new Exam(examId, examDate, coefficient, courseModel(courseId));

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(jCourse));
    when(examRepository.save(any(JExam.class))).thenReturn(savedEntity);
    when(examMapper.toModel(savedEntity)).thenReturn(model);

    ExamDTO result = examService.create(examDate, coefficient, courseId);

    ArgumentCaptor<JExam> captor = ArgumentCaptor.forClass(JExam.class);
    verify(examRepository).save(captor.capture());
    assertThat(captor.getValue().getExamDate()).isEqualTo(examDate);
    assertThat(captor.getValue().getCoefficient()).isEqualByComparingTo(coefficient);
    assertThat(captor.getValue().getCourse()).isEqualTo(jCourse);

    assertThat(result.courseId()).isEqualTo(courseId);
    assertThat(result.coefficient()).isEqualByComparingTo(coefficient);
  }

  @Test
  void should_throw_when_course_not_found_on_create() {
    UUID courseId = UUID.randomUUID();
    when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                examService.create(
                    Instant.parse("2026-06-15T09:00:00Z"), new BigDecimal("2.5"), courseId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(courseId.toString());
  }

  @Test
  void should_get_exam_by_id() {
    UUID examId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    Instant examDate = Instant.parse("2026-06-15T09:00:00Z");
    BigDecimal coefficient = new BigDecimal("2.5");

    JExam entity =
        JExam.builder()
            .id(examId)
            .examDate(examDate)
            .coefficient(coefficient)
            .course(jCourse(courseId))
            .build();
    Exam model = new Exam(examId, examDate, coefficient, courseModel(courseId));

    when(examRepository.findById(examId)).thenReturn(Optional.of(entity));
    when(examMapper.toModel(entity)).thenReturn(model);

    ExamDTO result = examService.getById(examId);
    assertThat(result.id()).isEqualTo(examId);
    assertThat(result.courseId()).isEqualTo(courseId);
  }

  @Test
  void should_throw_when_exam_not_found_by_id() {
    UUID examId = UUID.randomUUID();
    when(examRepository.findById(examId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> examService.getById(examId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(examId.toString());
  }

  @Test
  void should_find_by_course() {
    UUID courseId = UUID.randomUUID();
    UUID examId = UUID.randomUUID();
    Instant examDate = Instant.parse("2026-06-15T09:00:00Z");
    BigDecimal coefficient = new BigDecimal("2.5");

    JExam entity =
        JExam.builder()
            .id(examId)
            .examDate(examDate)
            .coefficient(coefficient)
            .course(jCourse(courseId))
            .build();
    Exam model = new Exam(examId, examDate, coefficient, courseModel(courseId));

    when(examRepository.findByCourseId(courseId)).thenReturn(List.of(entity));
    when(examMapper.toModel(entity)).thenReturn(model);

    List<ExamDTO> results = examService.findByCourse(courseId);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().courseId()).isEqualTo(courseId);
  }

  @Test
  void should_return_empty_list_when_no_exams_found_by_course() {
    UUID courseId = UUID.randomUUID();
    when(examRepository.findByCourseId(courseId)).thenReturn(List.of());

    assertThat(examService.findByCourse(courseId)).isEmpty();
  }

  @Test
  void should_get_course_id_of_exam() {
    UUID examId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    when(examRepository.findCourseIdByExamId(examId)).thenReturn(Optional.of(courseId));

    assertThat(examService.getCourseIdOf(examId)).isEqualTo(courseId);
  }

  @Test
  void should_throw_when_getting_course_id_of_unknown_exam() {
    UUID examId = UUID.randomUUID();
    when(examRepository.findCourseIdByExamId(examId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> examService.getCourseIdOf(examId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(examId.toString());
  }

  @Test
  void should_get_cursus_id_of_exam() {
    UUID examId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    when(examRepository.findCursusIdByExamId(examId)).thenReturn(Optional.of(cursusId));

    assertThat(examService.getCursusIdOf(examId)).isEqualTo(cursusId);
  }

  @Test
  void should_throw_when_getting_cursus_id_of_unknown_exam() {
    UUID examId = UUID.randomUUID();
    when(examRepository.findCursusIdByExamId(examId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> examService.getCursusIdOf(examId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(examId.toString());
  }
}