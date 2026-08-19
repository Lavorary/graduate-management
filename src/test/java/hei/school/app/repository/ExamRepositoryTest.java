package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ExamRepositoryTest extends FacadeIT {

  @Autowired private ExamRepository examRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private CursusRepository cursusRepository;

  @Test
  void should_find_by_course_id() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build());
    JCourse course =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref("ALG101")
                .title("Algorithmique")
                .credit(5)
                .build());
    examRepository.save(
        JExam.builder()
            .examDate(Instant.parse("2026-06-15T09:00:00Z"))
            .coefficient(new BigDecimal("2.5"))
            .course(course)
            .build());

    assertThat(examRepository.findByCourseId(course.getId())).hasSize(1);
  }
}
