package hei.school.app.conf.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.model.Cursus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CursusTest {

  @Test
  void should_expose_all_fields() {
    UUID id = UUID.randomUUID();
    Cursus cursus = new Cursus(id, "Développement Logiciel", "Cursus ingénieur logiciel");

    assertThat(cursus.id()).isEqualTo(id);
    assertThat(cursus.name()).isEqualTo("Développement Logiciel");
    assertThat(cursus.description()).isEqualTo("Cursus ingénieur logiciel");
  }

  @Test
  void should_be_equal_when_all_fields_match() {
    UUID id = UUID.randomUUID();
    Cursus c1 = new Cursus(id, "DevLog", "desc");
    Cursus c2 = new Cursus(id, "DevLog", "desc");

    assertThat(c1).isEqualTo(c2);
    assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
  }

  @Test
  void should_not_be_equal_when_id_differs() {
    Cursus c1 = new Cursus(UUID.randomUUID(), "DevLog", "desc");
    Cursus c2 = new Cursus(UUID.randomUUID(), "DevLog", "desc");

    assertThat(c1).isNotEqualTo(c2);
  }
}