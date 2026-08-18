package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.GroupRepository;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JGroup;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GroupDTOTest extends FacadeIT {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CursusRepository cursusRepository;

    @Test
    void should_create_group_dto_from_entity() {
        JCursus cursus = cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build()
        );

        JGroup saved = groupRepository.save(
            JGroup.builder()
                .ref("G1")
                .cursus(Set.of(cursus))
                .build()
        );

        GroupDTO dto = GroupDTO.builder()
            .id(saved.getId())
            .ref(saved.getRef())
            .cursusIds(Set.of(cursus.getId()))
            .build();

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(saved.getId());
        assertThat(dto.ref()).isEqualTo("G1");
        assertThat(dto.cursusIds()).hasSize(1);
        assertThat(dto.cursusIds()).contains(cursus.getId());
    }

    @Test
    void should_build_group_dto_with_builder() {
        UUID id = UUID.randomUUID();
        UUID cursusId = UUID.randomUUID();

        GroupDTO dto = GroupDTO.builder()
            .id(id)
            .ref("G2")
            .cursusIds(Set.of(cursusId))
            .build();

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.ref()).isEqualTo("G2");
        assertThat(dto.cursusIds()).hasSize(1);
        assertThat(dto.cursusIds()).contains(cursusId);
    }
}
