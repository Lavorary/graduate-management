package hei.school.app.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import hei.school.app.enums.Reason;
import lombok.Builder;

@Builder
public record ScoreHistory(UUID id, Grade grade, BigDecimal score, Instant gradedAt, Reason reason, String explanation) {

}
