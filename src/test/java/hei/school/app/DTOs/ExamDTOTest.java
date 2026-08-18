package hei.school.app.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.app.conf.FacadeIT;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JExam;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ExamDTOTest extends FacadeIT {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CursusRepository cursusRepository;

    @Test
    void should_create_exam_dto_from_entity() {
        JCursus cursus = cursusRepository.save(
            JCursus.builder().name("DevLog").description("d").year("2026").build()
        );
        JCourse course = courseRepository.save(
            JCourse.builder().cursus(cursus).ref("ALG101").title("Algorithmique").credit(5).build()
        );

        JExam saved = examRepository.save(
            JExam.builder()
                .examDate(Instant.parse("2026-06-15T09:00:00Z"))
                .coefficient(new BigDecimal("2.5"))
                .course(course)
                .build()
        );

        ExamDTO dto = ExamDTO.builder()
            .id(saved.getId())
            .examDate(saved.getExamDate())
            .coefficient(saved.getCoefficient())
            .courseId(saved.getCourse().getId())
            .build();

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(saved.getId());
        assertThat(dto.examDate()).isEqualTo(Instant.parse("2026-06-15T09:00:00Z"));
        assertThat(dto.coefficient()).isEqualTo(new BigDecimal("2.5"));
        assertThat(dto.courseId()).isEqualTo(course.getId());
    }

    @Test
    void should_build_exam_dto_with_builder() {
        UUID id = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Instant examDate = Instant.now();

        ExamDTO dto = ExamDTO.builder()
            .id(id)
            .examDate(examDate)
            .coefficient(new BigDecimal("1.5"))
            .courseId(courseId)
            .build();

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.examDate()).isEqualTo(examDate);
        assertThat(dto.coefficient()).isEqualTo(new BigDecimal("1.5"));
        assertThat(dto.courseId()).isEqualTo(courseId);
    }
}
