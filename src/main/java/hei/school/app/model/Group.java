package hei.school.app.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Group(UUID id, String ref, Cursus cursus) {}
