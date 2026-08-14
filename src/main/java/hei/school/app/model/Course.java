package hei.school.app.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Course(
        UUID id,
        String ref,
        String title,
        Integer credit
) {}
