package hei.school.app.DTOs;

import java.util.UUID;
import lombok.Builder;

@Builder
public record GradeDTO(UUID id, UUID examId, UUID studentId, UUID gradedById) {}
