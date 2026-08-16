package hei.school.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.model.Exam;
import hei.school.app.model.Grade;
import hei.school.app.model.User;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JUser;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class GradeMapperTest {
  @Autowired private GradeMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    JUser student = JUser.builder().id(UUID.randomUUID()).firstName("Student A").build();
    JUser teacher = JUser.builder().id(UUID.randomUUID()).firstName("Teacher B").build();
    JExam exam = JExam.builder().id(UUID.randomUUID()).build();

    JGrade jGrade =
        JGrade.builder()
            .id(UUID.randomUUID())
            .student(student)
            .gradedBy(teacher)
            .exam(exam)
            .build();

    Grade domain = mapper.toModel(jGrade);

    assertThat(domain.student().firstName()).isEqualTo("Student A");
    assertThat(domain.gradedBy().firstName()).isEqualTo("Teacher B");
    assertThat(domain.exam().id()).isEqualTo(exam.getId());
  }

  @Test
  void shouldMapDomainToJpa() {
    User student = User.builder().id(UUID.randomUUID()).firstName("Student C").build();
    User teacher = User.builder().id(UUID.randomUUID()).firstName("Teacher D").build();
    Exam exam = Exam.builder().id(UUID.randomUUID()).build();

    Grade domain =
        Grade.builder().id(UUID.randomUUID()).student(student).gradedBy(teacher).exam(exam).build();

    JGrade jGrade = mapper.toEntity(domain);

    assertThat(jGrade.getStudent().getFirstName()).isEqualTo("Student C");
    assertThat(jGrade.getGradedBy().getFirstName()).isEqualTo("Teacher D");
    assertThat(jGrade.getExam().getId()).isEqualTo(exam.id());
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JGrade> jList =
        List.of(
            JGrade.builder().id(UUID.randomUUID()).build(),
            JGrade.builder().id(UUID.randomUUID()).build());

    List<Grade> domains = mapper.toModel(jList);

    assertThat(domains).hasSize(2);
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<Grade> domains =
        List.of(
            Grade.builder().id(UUID.randomUUID()).build(),
            Grade.builder().id(UUID.randomUUID()).build());

    List<JGrade> jList = mapper.toEntity(domains);

    assertThat(jList).hasSize(2);
  }
}
