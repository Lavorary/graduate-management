package hei.school.app.service;

import hei.school.app.DTOs.GradeDTO;
import hei.school.app.mapper.GradeMapper;
import hei.school.app.model.Grade;
import hei.school.app.repository.ExamRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JExam;
import hei.school.app.repository.model.JGrade;
import hei.school.app.repository.model.JUser;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GradeService {

  private final GradeRepository gradeRepository;
  private final ExamRepository examRepository;
  private final UserRepository userRepository;
  private final GradeMapper gradeMapper;

  public GradeDTO create(UUID examId, UUID studentId, UUID gradedById) {
    JExam exam =
        examRepository
            .findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("Exam with Id : " + examId + " not found"));
    JUser student =
        userRepository
            .findById(studentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Student with Id : " + studentId + " not found"));
    JUser gradedBy =
        userRepository
            .findById(gradedById)
            .orElseThrow(
                () -> new IllegalArgumentException("Grader with Id : " + gradedById + " not found"));

    JGrade saved =
        gradeRepository.save(
            JGrade.builder().exam(exam).student(student).gradedBy(gradedBy).build());
    return toDto(saved);
  }

  public GradeDTO getById(UUID id) {
    JGrade entity =
        gradeRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Grade with Id : " + id + " not found"));
    return toDto(entity);
  }

  public List<GradeDTO> findByExam(UUID examId) {
    return gradeRepository.findByExam_Id(examId).stream().map(this::toDto).toList();
  }

  public List<GradeDTO> findByStudent(UUID studentId) {
    return gradeRepository.findByStudent_Id(studentId).stream().map(this::toDto).toList();
  }

  public List<GradeDTO> findByTeacher(UUID teacherId) {
    return gradeRepository.findByGradedBy_Id(teacherId).stream().map(this::toDto).toList();
  }

  private GradeDTO toDto(JGrade entity) {
    Grade model = gradeMapper.toModel(entity);
    return GradeDTO.builder()
        .id(model.id())
        .examId(model.exam().id())
        .studentId(model.student().id())
        .gradedById(model.gradedBy().id())
        .build();
  }
}