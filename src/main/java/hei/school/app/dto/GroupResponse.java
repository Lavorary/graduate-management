package hei.school.app.dto;

import java.util.Set;
import java.util.UUID;

public record GroupResponse(UUID id, String ref, Set<CursusResponse> cursus) {}
