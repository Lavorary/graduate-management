package hei.school.app.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.UUID;

@Builder
public record Exam(
        UUID id,
        Timestamp examDate,
        BigDecimal coefficient,
        Course course
) {}
