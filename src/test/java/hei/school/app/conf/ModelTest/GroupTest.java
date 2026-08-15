package hei.school.app.conf.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.model.Cursus;
import hei.school.app.model.Group;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GroupTest {

  @Test
  void should_expose_all_fields_including_linked_cursus() {
    Cursus cursus = new Cursus(UUID.randomUUID(), "DevLog", "desc");
    Group group = new Group(UUID.randomUUID(), "GROUPE-A", cursus);

    assertThat(group.ref()).isEqualTo("GROUPE-A");
    assertThat(group.cursus()).isEqualTo(cursus);
  }

  @Test
  void should_be_equal_when_all_fields_match() {
    UUID id = UUID.randomUUID();
    Cursus cursus = new Cursus(UUID.randomUUID(), "DevLog", "desc");
    Group g1 = new Group(id, "GROUPE-A", cursus);
    Group g2 = new Group(id, "GROUPE-A", cursus);

    assertThat(g1).isEqualTo(g2);
    assertThat(g1.hashCode()).isEqualTo(g2.hashCode());
  }
}
