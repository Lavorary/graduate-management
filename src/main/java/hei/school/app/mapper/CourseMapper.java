package hei.school.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hei.school.app.model.Course;
import hei.school.app.repository.model.JCourse;

@Mapper(
    componentModel = "spring",
    uses = {CursusMapper.class, UserMapper.class})
public interface CourseMapper {
  @Mapping(source = "cursus", target = "cursus")
  @Mapping(source = "teachers", target = "teachers")
  Course toModel(JCourse entity);

  JCourse toEntity(Course model);

  List<Course> toModel(List<JCourse> entities);

  List<JCourse> toEntity(List<Course> models);
}