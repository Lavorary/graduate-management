package hei.school.app.endpoint.rest.controller;

import hei.school.app.DTOs.GradeDTO;
import hei.school.app.dto.GradeSubmissionRequest;
import hei.school.app.security.model.Principal;
import hei.school.app.service.GradeService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
  private final GradeService gradeService;

  @PostMapping("/exams/{examId}")
  public ResponseEntity<GradeDTO> submit(
      @PathVariable UUID examId,
      @Valid @RequestBody GradeSubmissionRequest request,
      Authentication authentication) {
    var principal = (Principal) authentication.getPrincipal();
    var grade = gradeService.create(examId, request.studentId(), principal.user().id());
    return ResponseEntity.status(HttpStatus.CREATED).body(grade);
  }

  @GetMapping("/students/{studentId}")
  public ResponseEntity<List<GradeDTO>> getStudentGrades(@PathVariable UUID studentId) {
    var grades = gradeService.findByStudent(studentId);
    return ResponseEntity.ok(grades);
  }
}
