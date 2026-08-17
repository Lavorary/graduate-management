package hei.school.app.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Exam(UUID id, Instant examDate, BigDecimal coefficient, Course course) {}
