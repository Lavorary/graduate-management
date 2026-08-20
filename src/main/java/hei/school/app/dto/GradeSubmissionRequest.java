package hei.school.app.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GradeSubmissionRequest(UUID studentId, BigDecimal score) {}
