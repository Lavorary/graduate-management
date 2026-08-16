package hei.school.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hei.school.app.model.User;
import hei.school.app.repository.model.JUser;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toModel(JUser entity);

  @Mapping(target = "password", ignore = true)
  JUser toEntity(User model);

  List<User> toModel(List<JUser> entities);

  List<JUser> toEntity(List<User> models);
}