package hei.school.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.MapperTestConfig;
import hei.school.app.enums.Reason;
import hei.school.app.model.Grade;
import hei.school.app.model.ScoreHistory;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class ScoreHistoryMapperTest {
  @Autowired ScoreHistoryMapper mapper;

  @Test
  void shouldMapJpaToDomain() {
    JGrade jGrade = JGrade.builder().id(UUID.randomUUID()).build();
    JScoreHistory jHistory =
        JScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(jGrade)
            .score(BigDecimal.valueOf(85.0))
            .reason(Reason.CORRECTION)
            .explanation("Fixed error")
            .gradedAt(Instant.now())
            .build();

    ScoreHistory domain = mapper.toModel(jHistory);

    assertThat(domain.grade().id()).isEqualTo(jGrade.getId());
    assertThat(domain.score()).isEqualByComparingTo("85.0");
    assertThat(domain.reason()).isEqualTo(Reason.CORRECTION);
    assertThat(domain.explanation()).isEqualTo("Fixed error");
  }

  @Test
  void shouldMapDomainToJpa() {
    Grade grade = Grade.builder().id(UUID.randomUUID()).build();
    ScoreHistory domain =
        ScoreHistory.builder()
            .id(UUID.randomUUID())
            .grade(grade)
            .score(BigDecimal.valueOf(92.5))
            .reason(Reason.BONUS)
            .explanation("Extra credit")
            .gradedAt(Instant.now())
            .build();

    JScoreHistory jHistory = mapper.toEntity(domain);

    assertThat(jHistory.getGrade().getId()).isEqualTo(grade.id());
    assertThat(jHistory.getScore()).isEqualByComparingTo("92.5");
    assertThat(jHistory.getReason()).isEqualTo(Reason.BONUS);
    assertThat(jHistory.getExplanation()).isEqualTo("Extra credit");
  }

  @Test
  void shouldMapJpaListToDomainList() {
    List<JScoreHistory> jList =
        List.of(
            JScoreHistory.builder().id(UUID.randomUUID()).build(),
            JScoreHistory.builder().id(UUID.randomUUID()).build());

    List<ScoreHistory> domains = mapper.toModel(jList);

    assertThat(domains).hasSize(2);
  }

  @Test
  void shouldMapDomainListToJpaList() {
    List<ScoreHistory> domains =
        List.of(
            ScoreHistory.builder().id(UUID.randomUUID()).build(),
            ScoreHistory.builder().id(UUID.randomUUID()).build());

    List<JScoreHistory> jList = mapper.toEntity(domains);

    assertThat(jList).hasSize(2);
  }
}
