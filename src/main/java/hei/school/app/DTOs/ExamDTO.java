package hei.school.app.DTOs;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ExamDTO(UUID id, Instant examDate, BigDecimal coefficient, UUID courseId) {}
