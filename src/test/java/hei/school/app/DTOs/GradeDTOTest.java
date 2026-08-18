package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GradeDTOTest extends FacadeIT {

  @Autowired private GradeRepository gradeRepository;

  @Autowired private UserRepository userRepository;

  @Test
  void should_create_grade_dto_from_entity() {
    JUser student =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Student")
                .lastName("S")
                .role(UserRole.STUDENT)
                .email("student@hei.school")
                .password("x")
                .build());
    JUser teacher =
        userRepository.save(
            JUser.builder()
                .id(UUID.randomUUID())
                .firstName("Teacher")
                .lastName("T")
                .role(UserRole.TEACHER)
                .email("teacher@hei.school")
                .password("x")
                .build());

    JGrade saved =
        gradeRepository.save(JGrade.builder().student(student).gradedBy(teacher).build());

    GradeDTO dto =
        GradeDTO.builder()
            .id(saved.getId())
            .studentId(saved.getStudent().getId())
            .gradedById(saved.getGradedBy().getId())
            .build();

    assertThat(dto).isNotNull();
    assertThat(dto.id()).isEqualTo(saved.getId());
    assertThat(dto.studentId()).isEqualTo(student.getId());
    assertThat(dto.gradedById()).isEqualTo(teacher.getId());
  }

  @Test
  void should_build_grade_dto_with_builder() {
    UUID id = UUID.randomUUID();
    UUID examId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID gradedById = UUID.randomUUID();

    GradeDTO dto =
        GradeDTO.builder()
            .id(id)
            .examId(examId)
            .studentId(studentId)
            .gradedById(gradedById)
            .build();

    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.examId()).isEqualTo(examId);
    assertThat(dto.studentId()).isEqualTo(studentId);
    assertThat(dto.gradedById()).isEqualTo(gradedById);
  }
}
