package hei.school.app.model;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GroupMembership(
    UUID id, Timestamp startDate, Timestamp endDate, User user, Group group) {}
