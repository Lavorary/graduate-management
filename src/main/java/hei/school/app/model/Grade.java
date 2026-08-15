package hei.school.app.model;

import java.util.UUID;

public record Grade(UUID id, Exam exam, User student, User gradedBy) {}
