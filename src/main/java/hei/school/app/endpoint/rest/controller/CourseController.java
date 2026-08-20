package hei.school.app.endpoint.rest.controller;

import hei.school.app.DTOs.CourseDTO;
import hei.school.app.dto.CreateCourseRequest;
import hei.school.app.service.CourseService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
  private final CourseService courseService;

  @GetMapping("/{courseId}")
  public ResponseEntity<CourseDTO> get(@PathVariable UUID courseId) {
    var course = courseService.getById(courseId);
    return ResponseEntity.ok(course);
  }

  @PostMapping
  public ResponseEntity<CourseDTO> create(@Valid @RequestBody CreateCourseRequest request) {
    var course =
        courseService.create(
            request.cursusId(),
            request.ref(),
            request.title(),
            request.credit(),
            request.teacherIds());
    return ResponseEntity.status(HttpStatus.CREATED).body(course);
  }
}
