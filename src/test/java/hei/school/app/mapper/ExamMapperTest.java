package hei.school.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.model.Course;
import hei.school.app.model.Exam;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JExam;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class ExamMapperTest {
  @Autowired private ExamMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    JCourse jCourse = JCourse.builder().id(UUID.randomUUID()).title("Algorithms").build();
    JExam jExam =
        JExam.builder()
            .id(UUID.randomUUID())
            .examDate(Instant.now())
            .coefficient(BigDecimal.valueOf(1.5))
            .course(jCourse)
            .build();

    Exam domain = mapper.toModel(jExam);

    assertThat(domain.course().title()).isEqualTo("Algorithms");
    assertThat(domain.coefficient()).isEqualByComparingTo("1.5");
  }

  @Test
  void shouldMapDomainToJpa() {
    Course course = Course.builder().id(UUID.randomUUID()).title("Calculus").build();
    Exam domain =
        Exam.builder()
            .id(UUID.randomUUID())
            .examDate(Instant.now())
            .coefficient(BigDecimal.valueOf(2.0))
            .course(course)
            .build();

    JExam jExam = mapper.toEntity(domain);

    assertThat(jExam.getCourse().getTitle()).isEqualTo("Calculus");
    assertThat(jExam.getCoefficient()).isEqualByComparingTo("2.0");
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JExam> jList =
        List.of(
            JExam.builder().id(UUID.randomUUID()).build(),
            JExam.builder().id(UUID.randomUUID()).build());

    List<Exam> domains = mapper.toModel(jList);

    assertThat(domains).hasSize(2);
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<Exam> domains =
        List.of(
            Exam.builder().id(UUID.randomUUID()).build(),
            Exam.builder().id(UUID.randomUUID()).build());

    List<JExam> jList = mapper.toEntity(domains);

    assertThat(jList).hasSize(2);
  }
}
