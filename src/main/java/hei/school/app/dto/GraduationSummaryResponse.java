package hei.school.app.dto;

import java.util.UUID;

public record GraduationSummaryResponse(
    UUID cursusId, int totalStudents, long graduatedCount, double graduationPercentage) {}
