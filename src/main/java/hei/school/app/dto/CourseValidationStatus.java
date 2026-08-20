package hei.school.app.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CourseValidationStatus(
    UUID courseId, String title, int credits, boolean validated, BigDecimal averageScore) {}
