package hei.school.app.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Cursus(
       UUID id,
       String name,
       String description

) {}
