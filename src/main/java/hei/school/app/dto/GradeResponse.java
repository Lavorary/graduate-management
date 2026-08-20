package hei.school.app.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GradeResponse(
    UUID id,
    UUID examId,
    UUID studentId,
    UUID gradedBy,
    BigDecimal currentScore,
    Instant gradedAt) {}
