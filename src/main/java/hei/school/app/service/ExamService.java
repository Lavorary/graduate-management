package hei.school.app.service;

import hei.school.app.DTOs.ExamDTO;
import hei.school.app.mapper.ExamMapper;
import hei.school.app.model.Exam;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JExam;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {

  private final ExamRepository examRepository;
  private final CourseRepository courseRepository;
  private final ExamMapper examMapper;

  public ExamDTO create(Instant examDate, BigDecimal coefficient, UUID courseId) {
    JCourse course =
        courseRepository
            .findById(courseId)
            .orElseThrow(
                () -> new IllegalArgumentException("Course with Id : " + courseId + " not found"));

    JExam saved =
        examRepository.save(
            JExam.builder().examDate(examDate).coefficient(coefficient).course(course).build());
    return toDto(saved);
  }

  public ExamDTO getById(UUID id) {
    JExam entity =
        examRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Exam with Id : " + id + " not found"));
    return toDto(entity);
  }

  public List<ExamDTO> findByCourse(UUID courseId) {
    return examRepository.findByCourseId(courseId).stream().map(this::toDto).toList();
  }

  public UUID getCourseIdOf(UUID examId) {
    return examRepository
        .findCourseIdByExamId(examId)
        .orElseThrow(() -> new IllegalArgumentException("Exam with Id : " + examId + " not found"));
  }

  public UUID getCursusIdOf(UUID examId) {
    return examRepository
        .findCursusIdByExamId(examId)
        .orElseThrow(() -> new IllegalArgumentException("Exam with Id : " + examId + " not found"));
  }

  private ExamDTO toDto(JExam entity) {
    Exam model = examMapper.toModel(entity);
    return ExamDTO.builder()
        .id(model.id())
        .examDate(model.examDate())
        .coefficient(model.coefficient())
        .courseId(model.course().id())
        .build();
  }
}
