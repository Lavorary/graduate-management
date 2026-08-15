package hei.school.app.conf.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.model.Course;
import hei.school.app.model.Exam;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExamTest {

  @Test
  void should_expose_all_fields() {
    Course course = new Course(UUID.randomUUID(), null, "ALG101", "Algorithmique", 5);
    Timestamp date = Timestamp.valueOf("2026-06-15 09:00:00");
    Exam exam = new Exam(UUID.randomUUID(), date, new BigDecimal("2.5"), course);

    assertThat(exam.examDate()).isEqualTo(date);
    assertThat(exam.coefficient()).isEqualByComparingTo("2.5");
    assertThat(exam.course()).isEqualTo(course);
  }

  @Test
  void should_be_equal_when_all_fields_match() {
    UUID id = UUID.randomUUID();
    Timestamp date = Timestamp.valueOf("2026-06-15 09:00:00");
    Exam e1 = new Exam(id, date, new BigDecimal("2.5"), null);
    Exam e2 = new Exam(id, date, new BigDecimal("2.5"), null);

    assertThat(e1).isEqualTo(e2);
    assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
  }
}
