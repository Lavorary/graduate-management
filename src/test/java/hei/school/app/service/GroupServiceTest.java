package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.GroupDTO;
import hei.school.app.mapper.GroupMapper;
import hei.school.app.model.Cursus;
import hei.school.app.model.Group;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.GroupRepository;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JGroup;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  @Mock private GroupRepository groupRepository;
  @Mock private CursusRepository cursusRepository;
  @Mock private GroupMapper groupMapper;
  @InjectMocks private GroupService groupService;

  private JCursus jCursus(UUID id) {
    return JCursus.builder()
        .id(id)
        .name("DevLog")
        .description("Development Logic")
        .year("2026")
        .build();
  }

  private Cursus cursusModel(UUID id) {
    return new Cursus(id, "DevLog", "Development Logic", "2026");
  }

  @Test
  void should_create_group_with_cursus() {
    UUID groupId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();

    JCursus jCursus = jCursus(cursusId);
    JGroup savedEntity = JGroup.builder()
        .id(groupId)
        .ref("G1")
        .cursus(Set.of(jCursus))
        .build();

    Cursus cursus = cursusModel(cursusId);
    Group model = new Group(groupId, "G1", Set.of(cursus));

    when(cursusRepository.findById(cursusId)).thenReturn(Optional.of(jCursus));
    when(groupRepository.save(any(JGroup.class))).thenReturn(savedEntity);
    when(groupMapper.toModel(savedEntity)).thenReturn(model);

    GroupDTO result = groupService.create("G1", Set.of(cursusId));

    ArgumentCaptor<JGroup> captor = ArgumentCaptor.forClass(JGroup.class);
    verify(groupRepository).save(captor.capture());
    assertThat(captor.getValue().getRef()).isEqualTo("G1");
    assertThat(captor.getValue().getCursus()).containsExactly(jCursus);

    assertThat(result.ref()).isEqualTo("G1");
    assertThat(result.cursusIds()).containsExactly(cursusId);
  }

  @Test
  void should_create_group_without_cursus() {
    UUID groupId = UUID.randomUUID();

    JGroup savedEntity = JGroup.builder()
        .id(groupId)
        .ref("G1")
        .cursus(Set.of())
        .build();

    Group model = new Group(groupId, "G1", Set.of());

    when(groupRepository.save(any(JGroup.class))).thenReturn(savedEntity);
    when(groupMapper.toModel(savedEntity)).thenReturn(model);

    GroupDTO result = groupService.create("G1", null);

    assertThat(result.ref()).isEqualTo("G1");
    assertThat(result.cursusIds()).isEmpty();
    verify(cursusRepository, never()).findById(any());
  }

  @Test
  void should_throw_when_cursus_not_found_on_create() {
    UUID cursusId = UUID.randomUUID();
    when(cursusRepository.findById(cursusId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> groupService.create("G1", Set.of(cursusId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(cursusId.toString());
  }

  @Test
  void should_get_group_by_id() {
    UUID groupId = UUID.randomUUID();
    JGroup entity = JGroup.builder().id(groupId).ref("G1").build();
    Group model = new Group(groupId, "G1", null);

    when(groupRepository.findById(groupId)).thenReturn(Optional.of(entity));
    when(groupMapper.toModel(entity)).thenReturn(model);

    GroupDTO result = groupService.getById(groupId);
    assertThat(result.id()).isEqualTo(groupId);
    assertThat(result.ref()).isEqualTo("G1");
  }

  @Test
  void should_throw_when_group_not_found_by_id() {
    UUID groupId = UUID.randomUUID();
    when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> groupService.getById(groupId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(groupId.toString());
  }

  @Test
  void should_find_by_cursus() {
    UUID cursusId = UUID.randomUUID();
    UUID groupId = UUID.randomUUID();

    JCursus jCursus = jCursus(cursusId);
    JGroup entity = JGroup.builder()
        .id(groupId)
        .ref("G1")
        .cursus(Set.of(jCursus))
        .build();

    Cursus cursus = cursusModel(cursusId);
    Group model = new Group(groupId, "G1", Set.of(cursus));

    when(groupRepository.findByCursus_Id(cursusId)).thenReturn(List.of(entity));
    when(groupMapper.toModel(entity)).thenReturn(model);

    List<GroupDTO> results = groupService.findByCursus(cursusId);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().cursusIds()).contains(cursusId);
  }

  @Test
  void should_return_empty_list_when_no_groups_found_by_cursus() {
    UUID cursusId = UUID.randomUUID();
    when(groupRepository.findByCursus_Id(cursusId)).thenReturn(List.of());

    assertThat(groupService.findByCursus(cursusId)).isEmpty();
  }
}