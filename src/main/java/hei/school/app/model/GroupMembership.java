package hei.school.app.model;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
public record GroupMembership(
        UUID id,
        Timestamp startDate,
        Timestamp endDate,
        User user,
        Group group

) {}
