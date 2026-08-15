package hei.school.app.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Group(
        UUID id,
        String ref,
        Cursus cursus
) {}
