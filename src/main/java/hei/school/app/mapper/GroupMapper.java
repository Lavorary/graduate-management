package hei.school.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hei.school.app.model.Group;
import hei.school.app.repository.model.JGroup;

@Mapper(componentModel = "spring", uses = CursusMapper.class)
public interface GroupMapper {
  Group toModel(JGroup entity);

  JGroup toEntity(Group entity);

  List<Group> toModel(List<JGroup> entities);

  List<JGroup> toEntity(List<Group> models);
}