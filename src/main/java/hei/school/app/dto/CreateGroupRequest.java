package hei.school.app.dto;

import java.util.Set;
import java.util.UUID;

public record CreateGroupRequest(String ref, Set<UUID> cursusIds) {}
