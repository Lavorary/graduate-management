package hei.school.app.mapper;

import hei.school.app.model.Exam;
import hei.school.app.repository.model.JExam;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CourseMapper.class)
public interface ExamMapper {
  Exam toModel(JExam entity);

  JExam toEntity(Exam model);

  List<Exam> toModel(List<JExam> entities);

  List<JExam> toEntity(List<Exam> models);
}
