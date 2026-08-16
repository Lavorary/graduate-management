package hei.school.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hei.school.app.model.Cursus;
import hei.school.app.repository.model.JCursus;

@Mapper(componentModel = "spring")
public interface CursusMapper {
  Cursus toModel(JCursus entity);

  JCursus toEntity(Cursus model);

  List<Cursus> toModel(List<JCursus> entities);

  List<JCursus> toEntity(List<Cursus> models);
}