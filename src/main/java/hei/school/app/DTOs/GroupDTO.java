package hei.school.app.DTOs;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GroupDTO(UUID id, String ref, Set<UUID> cursusIds) {}
