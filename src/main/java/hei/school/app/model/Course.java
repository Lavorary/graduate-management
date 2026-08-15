package hei.school.app.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Course(UUID id, Cursus cursus, String ref, String title, Integer credit) {}
