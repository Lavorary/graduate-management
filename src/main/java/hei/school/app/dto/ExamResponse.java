package hei.school.app.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExamResponse(
    UUID id, Instant examDate, BigDecimal coefficient, CourseResponse course) {}
