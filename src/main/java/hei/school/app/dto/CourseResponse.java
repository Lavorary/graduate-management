package hei.school.app.dto;

import java.util.Set;
import java.util.UUID;

public record CourseResponse(
    UUID id,
    String ref,
    String title,
    int credit,
    CursusResponse cursus,
    Set<UserResponse> teachers) {}
