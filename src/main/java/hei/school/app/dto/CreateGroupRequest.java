package hei.school.app.dto;

import java.util.UUID;

public record CreateGroupRequest(String ref, UUID cursusId) {}
