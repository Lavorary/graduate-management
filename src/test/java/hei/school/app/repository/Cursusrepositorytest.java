package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCursus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CursusRepositoryTest extends FacadeIT {

  @Autowired private CursusRepository cursusRepository;

  @Test
  void should_save_and_find_cursus_by_id() {
    JCursus cursus =
        cursusRepository.save(
            JCursus.builder().name("Développement Logiciel").description("Cursus ingénieur").year("2026").build());

    assertThat(cursusRepository.findById(cursus.getId())).isPresent();
    assertThat(cursusRepository.findById(cursus.getId()).get().getName())
        .isEqualTo("Développement Logiciel");
  }

  @Test
  void should_find_by_year() {
    cursusRepository.save(JCursus.builder().name("DevLog A").description("d").year("2026").build());
    cursusRepository.save(JCursus.builder().name("DevLog B").description("d").year("2027").build());

    assertThat(cursusRepository.findByYear("2026")).extracting(JCursus::getName).containsExactly("DevLog A");
  }
}