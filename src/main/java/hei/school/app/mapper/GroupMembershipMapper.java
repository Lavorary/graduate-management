package hei.school.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hei.school.app.model.GroupMembership;
import hei.school.app.repository.model.JGroupMembership;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, GroupMapper.class})
public interface GroupMembershipMapper {
  GroupMembership toModel(JGroupMembership entity);

  JGroupMembership toEntity(GroupMembership model);

  List<GroupMembership> toModel(List<JGroupMembership> entities);

  List<JGroupMembership> toEntity(List<GroupMembership> models);
}