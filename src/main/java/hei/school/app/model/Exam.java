package hei.school.app.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Exam(UUID id, Timestamp examDate, BigDecimal coefficient, Course course) {}
