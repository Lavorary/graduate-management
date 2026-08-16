package hei.school.app.mapper;

import hei.school.app.model.Grade;
import hei.school.app.repository.model.JGrade;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {ExamMapper.class, UserMapper.class})
public interface GradeMapper {
  Grade toModel(JGrade entity);

  JGrade toEntity(Grade model);

  List<Grade> toModel(List<JGrade> entities);

  List<JGrade> toEntity(List<Grade> models);
}
