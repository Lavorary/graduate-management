package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.model.JCursus;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CursusDTOTest extends FacadeIT {

    @Autowired
    private CursusRepository cursusRepository;

    @Test
    void should_create_cursus_dto_from_entity() {
        JCursus saved = cursusRepository.save(
            JCursus.builder()
                .name("DevLog")
                .description("Development and Logistics")
                .year("2026")
                .build()
        );

        CursusDTO dto = CursusDTO.builder()
            .id(saved.getId())
            .name(saved.getName())
            .description(saved.getDescription())
            .year(saved.getYear())
            .build();

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(saved.getId());
        assertThat(dto.name()).isEqualTo("DevLog");
        assertThat(dto.description()).isEqualTo("Development and Logistics");
        assertThat(dto.year()).isEqualTo("2026");
    }

    @Test
    void should_build_cursus_dto_with_builder() {
        UUID id = UUID.randomUUID();
        CursusDTO dto = CursusDTO.builder()
            .id(id)
            .name("DataScience")
            .description("Data Science Program")
            .year("2025")
            .build();

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.name()).isEqualTo("DataScience");
        assertThat(dto.description()).isEqualTo("Data Science Program");
        assertThat(dto.year()).isEqualTo("2025");
    }
}
