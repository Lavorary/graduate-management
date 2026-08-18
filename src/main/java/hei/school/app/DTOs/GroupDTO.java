package hei.school.app.DTOs;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record GroupDTO(UUID id, String ref, Set<UUID> cursusIds) {}
