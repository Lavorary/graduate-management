package hei.school.app.mapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.model.Cursus;
import hei.school.app.repository.model.JCursus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class CursusMapperTest {
  @Autowired private CursusMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    JCursus jCursus =
        JCursus.builder()
            .id(UUID.randomUUID())
            .name("CS")
            .description("Computer Science")
            .year("2026")
            .build();

    Cursus domain = mapper.toModel(jCursus);

    assertThat(domain.name()).isEqualTo("CS");
    assertThat(domain.description()).isEqualTo("Computer Science");
  }

  @Test
  void shouldMapDomainToJpa() {
    Cursus domain =
        Cursus.builder().id(UUID.randomUUID()).name("Math").description("Mathematics").build();

    JCursus jCursus = mapper.toEntity(domain);

    assertThat(jCursus.getName()).isEqualTo("Math");
    assertThat(jCursus.getDescription()).isEqualTo("Mathematics");
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JCursus> jList =
        List.of(
            JCursus.builder().id(UUID.randomUUID()).name("CS").build(),
            JCursus.builder().id(UUID.randomUUID()).name("Math").build());

    List<Cursus> domains = mapper.toModel(jList);

    assertThat(domains).hasSize(2);
    assertThat(domains.get(0).name()).isEqualTo("CS");
    assertThat(domains.get(1).name()).isEqualTo("Math");
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<Cursus> domains =
        List.of(
            Cursus.builder().id(UUID.randomUUID()).name("CS").build(),
            Cursus.builder().id(UUID.randomUUID()).name("Math").build());

    List<JCursus> jList = mapper.toEntity(domains);

    assertThat(jList).hasSize(2);
    assertThat(jList.get(0).getName()).isEqualTo("CS");
    assertThat(jList.get(1).getName()).isEqualTo("Math");
  }
}
