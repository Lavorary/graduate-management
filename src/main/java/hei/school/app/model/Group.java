package hei.school.app.model;

import java.util.Set;
import java.util.UUID;

import lombok.Builder;

@Builder
public record Group(UUID id, String ref, Set<Cursus> cursus) {

}
