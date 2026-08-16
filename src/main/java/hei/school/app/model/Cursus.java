package hei.school.app.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Cursus(UUID id, String name, String description, String year) {}
