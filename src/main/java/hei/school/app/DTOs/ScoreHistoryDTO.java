package hei.school.app.DTOs;

import hei.school.app.enums.Reason;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ScoreHistoryDTO(
    UUID id, UUID gradeId, BigDecimal score, Instant gradedAt, Reason reason, String explanation) {}
