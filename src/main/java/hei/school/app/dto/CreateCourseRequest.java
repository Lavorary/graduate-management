package hei.school.app.dto;

import java.util.Set;
import java.util.UUID;

public record CreateCourseRequest(String ref, String title, int credit, Set<UUID> teacherIds) {}
