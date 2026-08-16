package hei.school.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hei.school.app.model.ScoreHistory;
import hei.school.app.repository.model.JScoreHistory;

@Mapper(componentModel = "spring", uses = GradeMapper.class)
public interface ScoreHistoryMapper {
  ScoreHistory toModel(JScoreHistory entity);

  JScoreHistory toEntity(ScoreHistory model);

  List<ScoreHistory> toModel(List<JScoreHistory> entities);

  List<JScoreHistory> toEntity(List<ScoreHistory> models);
}