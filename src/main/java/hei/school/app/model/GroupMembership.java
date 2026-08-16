package hei.school.app.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

@Builder
public record GroupMembership(UUID id, LocalDate startDate, LocalDate endDate, User student, Group group) {

}
