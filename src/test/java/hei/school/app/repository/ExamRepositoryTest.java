package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ExamRepositoryTest extends FacadeIT {

  @Autowired private ExamRepository examRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private CursusRepository cursusRepository;

  private JExam saveExam(JCourse course) {
    return examRepository.save(
        JExam.builder()
            .examDate(Instant.parse("2026-06-15T09:00:00Z"))
            .coefficient(new BigDecimal("2.5"))
            .course(course)
            .build());
  }

  @Test
  void should_find_by_course_id() {
    JCursus cursus = cursusRepository.save(JCursus.builder().name("DevLog").description("d").year("2026").build());
    JCourse course =
        courseRepository.save(JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build());
    saveExam(course);

    assertThat(examRepository.findByCourseId(course.getId())).hasSize(1);
  }

  @Test
  void should_find_course_id_by_exam_id() {
    JCursus cursus = cursusRepository.save(JCursus.builder().name("DevLog").description("d").year("2026").build());
    JCourse course =
        courseRepository.save(JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build());
    JExam exam = saveExam(course);

    assertThat(examRepository.findCourseIdByExamId(exam.getId())).contains(course.getId());
  }

  @Test
  void should_return_empty_when_exam_does_not_exist_for_course_id() {
    assertThat(examRepository.findCourseIdByExamId(UUID.randomUUID())).isEmpty();
  }

  @Test
  void should_find_cursus_id_by_exam_id() {
    JCursus cursus = cursusRepository.save(JCursus.builder().name("DevLog").description("d").year("2026").build());
    JCourse course =
        courseRepository.save(JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build());
    JExam exam = saveExam(course);

    assertThat(examRepository.findCursusIdByExamId(exam.getId())).contains(cursus.getId());
  }

  @Test
  void should_return_empty_when_exam_does_not_exist_for_cursus_id() {
    assertThat(examRepository.findCursusIdByExamId(UUID.randomUUID())).isEmpty();
  }
}