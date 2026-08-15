package hei.school.app.conf.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.model.Grade;
import hei.school.app.enums.Reason;
import hei.school.app.model.ScoreHistory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ScoreHistoryTest {

  @Test
  void should_expose_all_fields() {
    Grade grade = new Grade(UUID.randomUUID(), null, null, null);
    Timestamp gradedAt = Timestamp.valueOf("2026-06-20 10:00:00");

    ScoreHistory entry =
        new ScoreHistory(
            UUID.randomUUID(),
            grade,
            new BigDecimal("14.50"),
            gradedAt,
            Reason.CLAIM,
            "Erreur de saisie corrigée après réclamation de l'étudiant");

    assertThat(entry.grade()).isEqualTo(grade);
    assertThat(entry.score()).isEqualByComparingTo("14.50");
    assertThat(entry.gradedAt()).isEqualTo(gradedAt);
    assertThat(entry.reason()).isEqualTo(Reason.CLAIM);
    assertThat(entry.explanation()).contains("réclamation");
  }

  @Test
  void should_be_equal_when_all_fields_match() {
    UUID id = UUID.randomUUID();
    Timestamp gradedAt = Timestamp.valueOf("2026-06-20 10:00:00");
    ScoreHistory s1 = new ScoreHistory(id, null, new BigDecimal("14.50"), gradedAt, Reason.OTHER, "x");
    ScoreHistory s2 = new ScoreHistory(id, null, new BigDecimal("14.50"), gradedAt, Reason.OTHER, "x");

    assertThat(s1).isEqualTo(s2);
    assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
  }
}