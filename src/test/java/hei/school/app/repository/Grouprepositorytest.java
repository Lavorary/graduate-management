package hei.school.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JGroup;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GroupRepositoryTest extends FacadeIT {

  @Autowired private GroupRepository groupRepository;
  @Autowired private CursusRepository cursusRepository;

  @Test
  void should_find_by_ref() {
    groupRepository.save(JGroup.builder().ref("GROUPE-A").build());

    assertThat(groupRepository.findByRef("GROUPE-A")).isPresent();
  }

  @Test
  void should_find_by_cursus_id() {
    JCursus cursus = cursusRepository.save(JCursus.builder().name("DevLog").description("d").year("2026").build());
    groupRepository.save(JGroup.builder().ref("GROUPE-A").cursus(Set.of(cursus)).build());

    assertThat(groupRepository.findByCursus_Id(cursus.getId())).hasSize(1);
  }
}