package hei.school.app.DTOs;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CursusDTO(UUID id, String name, String description, String year) {}