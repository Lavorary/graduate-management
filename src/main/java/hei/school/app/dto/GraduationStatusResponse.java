package hei.school.app.dto;

import java.util.List;
import java.util.UUID;

public record GraduationStatusResponse(
    UUID studentId,
    String studentName,
    UUID cursusId,
    boolean graduated,
    List<CourseValidationStatus> courseStatuses) {}
