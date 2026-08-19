package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.CursusDTO;
import hei.school.app.mapper.CursusMapper;
import hei.school.app.model.Cursus;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.model.JCursus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CursusServiceTest {

  @Mock private CursusRepository cursusRepository;
  @Mock private CursusMapper cursusMapper;
  @InjectMocks private CursusService cursusService;

  private JCursus jCursus(UUID id) {
    return JCursus.builder()
        .id(id)
        .name("DevLog")
        .description("Development Logic")
        .year("2026")
        .build();
  }

  @Test
  void should_create_cursus() {
    UUID id = UUID.randomUUID();
    JCursus savedEntity = jCursus(id);
    Cursus model = new Cursus(id, "DevLog", "Development Logic", "2026");

    when(cursusRepository.save(any(JCursus.class))).thenReturn(savedEntity);
    when(cursusMapper.toModel(savedEntity)).thenReturn(model);

    CursusDTO result = cursusService.create("DevLog", "Development Logic", "2026");

    ArgumentCaptor<JCursus> captor = ArgumentCaptor.forClass(JCursus.class);
    verify(cursusRepository).save(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("DevLog");
    assertThat(captor.getValue().getDescription()).isEqualTo("Development Logic");
    assertThat(captor.getValue().getYear()).isEqualTo("2026");

    assertThat(result.name()).isEqualTo("DevLog");
    assertThat(result.description()).isEqualTo("Development Logic");
    assertThat(result.year()).isEqualTo("2026");
  }

  @Test
  void should_get_cursus_by_id() {
    UUID id = UUID.randomUUID();
    JCursus entity = jCursus(id);
    Cursus model = new Cursus(id, "DevLog", "Development Logic", "2026");

    when(cursusRepository.findById(id)).thenReturn(Optional.of(entity));
    when(cursusMapper.toModel(entity)).thenReturn(model);

    CursusDTO result = cursusService.getById(id);
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.name()).isEqualTo("DevLog");
  }

  @Test
  void should_throw_when_cursus_not_found_by_id() {
    UUID id = UUID.randomUUID();
    when(cursusRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cursusService.getById(id))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(id.toString());
  }

  @Test
  void should_find_by_year() {
    UUID id = UUID.randomUUID();
    String year = "2026";
    JCursus entity = jCursus(id);
    Cursus model = new Cursus(id, "DevLog", "Development Logic", year);

    when(cursusRepository.findByYear(year)).thenReturn(List.of(entity));
    when(cursusMapper.toModel(entity)).thenReturn(model);

    List<CursusDTO> results = cursusService.findByYear(year);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().year()).isEqualTo(year);
  }

  @Test
  void should_return_empty_list_when_no_cursus_found_by_year() {
    String year = "2026";
    when(cursusRepository.findByYear(year)).thenReturn(List.of());

    assertThat(cursusService.findByYear(year)).isEmpty();
  }

  @Test
  void should_find_all() {
    UUID id = UUID.randomUUID();
    JCursus entity = jCursus(id);
    Cursus model = new Cursus(id, "DevLog", "Development Logic", "2026");

    when(cursusRepository.findAll()).thenReturn(List.of(entity));
    when(cursusMapper.toModel(entity)).thenReturn(model);

    List<CursusDTO> results = cursusService.findAll();
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().id()).isEqualTo(id);
  }

  @Test
  void should_return_empty_list_when_no_cursus_found() {
    when(cursusRepository.findAll()).thenReturn(List.of());

    assertThat(cursusService.findAll()).isEmpty();
  }
}