package hei.school.app.model;

import java.util.UUID;

import lombok.Builder;

@Builder
public record Grade(UUID id, Exam exam, User student, User gradedBy) {

}
