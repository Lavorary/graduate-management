package hei.school.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.model.Cursus;
import hei.school.app.model.Group;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JGroup;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class GroupMapperTest {
  @Autowired private GroupMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    JCursus c1 = JCursus.builder().id(UUID.randomUUID()).name("CS").build();
    JCursus c2 = JCursus.builder().id(UUID.randomUUID()).name("Math").build();

    JGroup jGroup = JGroup.builder().id(UUID.randomUUID()).ref("G1").cursus(Set.of(c1, c2)).build();

    Group domain = mapper.toModel(jGroup);

    assertThat(domain.ref()).isEqualTo("G1");
    assertThat(domain.cursus()).hasSize(2);
    assertThat(domain.cursus()).extracting("name").containsExactlyInAnyOrder("CS", "Math");
  }

  @Test
  void shouldMapDomainToJpa() {
    Cursus c1 = Cursus.builder().id(UUID.randomUUID()).name("CS").build();
    Cursus c2 = Cursus.builder().id(UUID.randomUUID()).name("Physics").build();

    Group domain = Group.builder().id(UUID.randomUUID()).ref("G2").cursus(Set.of(c1, c2)).build();

    JGroup jGroup = mapper.toEntity(domain);

    assertThat(jGroup.getRef()).isEqualTo("G2");
    assertThat(jGroup.getCursus()).hasSize(2);
    assertThat(jGroup.getCursus()).extracting("name").containsExactlyInAnyOrder("CS", "Physics");
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JGroup> jList =
        List.of(
            JGroup.builder().id(UUID.randomUUID()).ref("G1").build(),
            JGroup.builder().id(UUID.randomUUID()).ref("G2").build());

    List<Group> domains = mapper.toModel(jList);

    assertThat(domains).hasSize(2);
    assertThat(domains.get(0).ref()).isEqualTo("G1");
    assertThat(domains.get(1).ref()).isEqualTo("G2");
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<Group> domains =
        List.of(
            Group.builder().id(UUID.randomUUID()).ref("G1").build(),
            Group.builder().id(UUID.randomUUID()).ref("G2").build());

    List<JGroup> jList = mapper.toEntity(domains);

    assertThat(jList).hasSize(2);
    assertThat(jList.get(0).getRef()).isEqualTo("G1");
    assertThat(jList.get(1).getRef()).isEqualTo("G2");
  }
}
