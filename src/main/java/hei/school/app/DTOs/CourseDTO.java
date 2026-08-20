package hei.school.app.DTOs;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CourseDTO(
    UUID id, UUID cursusId, String ref, String title, int credit, Set<UUID> teacherIds) {}
