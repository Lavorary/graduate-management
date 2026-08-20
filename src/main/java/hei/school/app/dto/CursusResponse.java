package hei.school.app.dto;

import java.util.UUID;

public record CursusResponse(UUID id, String name, String description, String year) {}
