package hei.school.app.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record Exam(
        UUID id,
        Instant examDate,
        BigDecimal coefficient,
        Course course
) {}
