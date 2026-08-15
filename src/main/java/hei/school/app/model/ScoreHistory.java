package hei.school.app.model;

import hei.school.app.enums.Reason;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record ScoreHistory(
        UUID id,
        Grade grade,
        BigDecimal score,
        Timestamp gradedAt,
        Reason reason,
        String explanation) {}