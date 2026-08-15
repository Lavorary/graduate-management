package hei.school.app.conf.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.model.Course;
import hei.school.app.model.Cursus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CourseTest {

  @Test
  void should_expose_all_fields_including_linked_cursus() {
    Cursus cursus = new Cursus(UUID.randomUUID(), "DevLog", "desc");
    Course course = new Course(UUID.randomUUID(), cursus, "ALG101", "Algorithmique", 5);

    assertThat(course.cursus()).isEqualTo(cursus);
    assertThat(course.ref()).isEqualTo("ALG101");
    assertThat(course.title()).isEqualTo("Algorithmique");
    assertThat(course.credit()).isEqualTo(5);
  }

  @Test
  void should_be_equal_when_all_fields_match() {
    UUID id = UUID.randomUUID();
    Cursus cursus = new Cursus(UUID.randomUUID(), "DevLog", "desc");
    Course c1 = new Course(id, cursus, "ALG101", "Algo", 5);
    Course c2 = new Course(id, cursus, "ALG101", "Algo", 5);

    assertThat(c1).isEqualTo(c2);
    assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
  }
}
