package hei.school.app.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateExamRequest(Instant examDate, BigDecimal coefficient) {}
