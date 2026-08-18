package hei.school.app.DTOs;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GroupMembershipDTO(
    UUID id, LocalDate startDate, LocalDate endDate, UUID studentId, UUID groupId) {}
