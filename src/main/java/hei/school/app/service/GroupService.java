package hei.school.app.service;

import hei.school.app.DTOs.GroupDTO;
import hei.school.app.mapper.GroupMapper;
import hei.school.app.model.Cursus;
import hei.school.app.model.Group;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.GroupRepository;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JGroup;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;
  private final CursusRepository cursusRepository;
  private final GroupMapper groupMapper;

  public GroupDTO create(String ref, Set<UUID> cursusIds) {
    Set<JCursus> cursusSet = new HashSet<>();
    if (cursusIds != null) {
      for (UUID cursusId : cursusIds) {
        cursusSet.add(
            cursusRepository
                .findById(cursusId)
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Cursus with id : " + cursusId + "  not found")));
      }
    }
    JGroup saved = groupRepository.save(JGroup.builder().ref(ref).cursus(cursusSet).build());
    return toDto(saved);
  }

  public GroupDTO getById(UUID id) {
    JGroup entity =
        groupRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Group with Id : " + id + " not found"));
    return toDto(entity);
  }

  public List<GroupDTO> findByCursus(UUID cursusId) {
    return groupRepository.findByCursus_Id(cursusId).stream().map(this::toDto).toList();
  }

  private GroupDTO toDto(JGroup entity) {
    Group model = groupMapper.toModel(entity);
    Set<UUID> cursusIds =
        model.cursus() == null
            ? Set.of()
            : model.cursus().stream().map(Cursus::id).collect(Collectors.toSet());
    return GroupDTO.builder().id(model.id()).ref(model.ref()).cursusIds(cursusIds).build();
  }
}
