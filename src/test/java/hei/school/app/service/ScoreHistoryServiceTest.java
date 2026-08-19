package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.ScoreHistoryDTO;
import hei.school.app.enums.Reason;
import hei.school.app.mapper.ScoreHistoryMapper;
import hei.school.app.model.Course;
import hei.school.app.model.Cursus;
import hei.school.app.model.Exam;
import hei.school.app.model.Grade;
import hei.school.app.model.ScoreHistory;
import hei.school.app.model.User;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.ScoreHistoryRepository;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JScoreHistory;
import hei.school.app.security.model.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScoreHistoryServiceTest {

  @Mock private ScoreHistoryRepository scoreHistoryRepository;
  @Mock private GradeRepository gradeRepository;
  @Mock private ScoreHistoryMapper scoreHistoryMapper;
  @InjectMocks private ScoreHistoryService scoreHistoryService;

  private Grade createGradeModel(UUID id) {
    Course course = new Course(UUID.randomUUID(),
        new Cursus(UUID.randomUUID(), "CS", "desc", "2024"), "REF", "Title", 5, null);
    Exam exam = new Exam(UUID.randomUUID(), Instant.now(), BigDecimal.ONE, course);
    User student = new User(UUID.randomUUID(), "Student", "One", UserRole.STUDENT, "s@h.school", "pwd");
    User grader = new User(UUID.randomUUID(), "Teacher", "One", UserRole.TEACHER, "t@h.school", "pwd");
    return new Grade(id, exam, student, grader);
  }

  @Test
  void should_create_score_history() {
    UUID gradeId = UUID.randomUUID();
    UUID historyId = UUID.randomUUID();
    BigDecimal score = BigDecimal.valueOf(85.5);
    Reason reason = Reason.INITIAL;
    String explanation = "First exam attempt";

    JGrade jGrade = JGrade.builder().id(gradeId).build();
    JScoreHistory savedEntity = JScoreHistory.builder()
        .id(historyId)
        .grade(jGrade)
        .score(score)
        .reason(reason)
        .explanation(explanation)
        .build();

    Grade grade = createGradeModel(gradeId);
    ScoreHistory model = new ScoreHistory(historyId, grade, score, Instant.now(), reason, explanation);

    when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(jGrade));
    when(scoreHistoryRepository.save(any(JScoreHistory.class))).thenReturn(savedEntity);
    when(scoreHistoryMapper.toModel(savedEntity)).thenReturn(model);

    ScoreHistoryDTO result = scoreHistoryService.create(gradeId, score, reason, explanation);

    ArgumentCaptor<JScoreHistory> captor = ArgumentCaptor.forClass(JScoreHistory.class);
    verify(scoreHistoryRepository).save(captor.capture());
    assertThat(captor.getValue().getScore()).isEqualTo(score);
    assertThat(captor.getValue().getReason()).isEqualTo(reason);
    assertThat(captor.getValue().getExplanation()).isEqualTo(explanation);

    assertThat(result.gradeId()).isEqualTo(gradeId);
    assertThat(result.score()).isEqualTo(score);
    assertThat(result.reason()).isEqualTo(reason);
    assertThat(result.explanation()).isEqualTo(explanation);
  }

  @Test
  void should_throw_when_grade_not_found_on_create() {
    UUID gradeId = UUID.randomUUID();
    when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> scoreHistoryService.create(gradeId, BigDecimal.TEN, Reason.INITIAL, "test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(gradeId.toString());
  }

  @Test
  void should_get_score_history_by_id() {
    UUID historyId = UUID.randomUUID();
    UUID gradeId = UUID.randomUUID();
    BigDecimal score = BigDecimal.valueOf(90.0);
    Reason reason = Reason.CORRECTION;
    String explanation = "Corrected grade";

    JGrade jGrade = JGrade.builder().id(gradeId).build();
    JScoreHistory entity = JScoreHistory.builder()
        .id(historyId)
        .grade(jGrade)
        .score(score)
        .reason(reason)
        .explanation(explanation)
        .build();

    Grade grade = createGradeModel(gradeId);
    ScoreHistory model = new ScoreHistory(historyId, grade, score, Instant.now(), reason, explanation);

    when(scoreHistoryRepository.findById(historyId)).thenReturn(Optional.of(entity));
    when(scoreHistoryMapper.toModel(entity)).thenReturn(model);

    ScoreHistoryDTO result = scoreHistoryService.getById(historyId);
    assertThat(result.id()).isEqualTo(historyId);
    assertThat(result.gradeId()).isEqualTo(gradeId);
    assertThat(result.score()).isEqualTo(score);
  }

  @Test
  void should_throw_when_score_history_not_found_by_id() {
    UUID historyId = UUID.randomUUID();
    when(scoreHistoryRepository.findById(historyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> scoreHistoryService.getById(historyId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(historyId.toString());
  }

  @Test
  void should_find_by_grade() {
    UUID gradeId = UUID.randomUUID();
    UUID historyId = UUID.randomUUID();
    BigDecimal score = BigDecimal.valueOf(85.0);

    JGrade jGrade = JGrade.builder().id(gradeId).build();
    JScoreHistory entity = JScoreHistory.builder()
        .id(historyId)
        .grade(jGrade)
        .score(score)
        .build();

    Grade grade = createGradeModel(gradeId);
    ScoreHistory model = new ScoreHistory(historyId, grade, score, Instant.now(), null, null);

    when(scoreHistoryRepository.findByGradeIdOrderByGradedAtAsc(gradeId))
        .thenReturn(List.of(entity));
    when(scoreHistoryMapper.toModel(entity)).thenReturn(model);

    List<ScoreHistoryDTO> results = scoreHistoryService.findByGrade(gradeId);
    assertThat(results).hasSize(1);
    assertThat(results.get(0).gradeId()).isEqualTo(gradeId);
    assertThat(results.get(0).score()).isEqualTo(score);
  }

  @Test
  void should_return_empty_list_when_no_score_histories_found_by_grade() {
    UUID gradeId = UUID.randomUUID();
    when(scoreHistoryRepository.findByGradeIdOrderByGradedAtAsc(gradeId))
        .thenReturn(List.of());

    assertThat(scoreHistoryService.findByGrade(gradeId)).isEmpty();
  }
}