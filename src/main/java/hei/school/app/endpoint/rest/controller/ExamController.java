package hei.school.app.endpoint.rest.controller;

import hei.school.app.DTOs.ExamDTO;
import hei.school.app.dto.CreateExamRequest;
import hei.school.app.service.ExamService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {
  private final ExamService examService;

  @GetMapping("/{id}")
  public ResponseEntity<ExamDTO> get(@PathVariable UUID id) {
    var exam = examService.getById(id);
    return ResponseEntity.ok(exam);
  }

  @PostMapping("/courses/{courseId}")
  public ResponseEntity<ExamDTO> create(
      @PathVariable UUID courseId, @Valid @RequestBody CreateExamRequest request) {
    var exam = examService.create(request.examDate(), request.coefficient(), courseId);
    return ResponseEntity.status(HttpStatus.CREATED).body(exam);
  }
}
