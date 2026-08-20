package hei.school.app.dto;

import hei.school.app.enums.Reason;
import java.math.BigDecimal;

public record GradeUpdateRequest(BigDecimal score, Reason reason, String explanation) {}
