package hei.school.app.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExamScoreDetail(
    UUID examId,
    Instant examDate,
    BigDecimal coefficient,
    BigDecimal score,
    BigDecimal weightedContribution) {}
