package hei.school.app.endpoint.rest.controller;

import hei.school.app.dto.CourseValidationStatus;
import hei.school.app.dto.ExamScoreDetail;
import hei.school.app.dto.GraduationStatusResponse;
import hei.school.app.dto.GraduationSummaryResponse;
import hei.school.app.model.Cursus;
import hei.school.app.repository.UserRepository;
import hei.school.app.service.GraduationService;
import hei.school.app.service.GroupMembershipService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/graduation")
@RequiredArgsConstructor
public class GraduationController {
  private final GraduationService graduationService;
  private final GroupMembershipService groupMembershipService;
  private final UserRepository userRepository;

  @GetMapping("/students/{studentId}/check")
  public ResponseEntity<GraduationStatusResponse> checkGraduation(@PathVariable UUID studentId) {
    var student =
        userRepository
            .findById(studentId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    var active =
        groupMembershipService
            .findActiveByStudent(studentId)
            .orElseThrow(() -> new IllegalStateException("Student has no active group"));
    Optional<Cursus> cursus = active.group().cursus().stream().findFirst();
    var cursusId = cursus.orElseThrow(() -> new IllegalStateException("No cursus found")).id();
    var graduated = graduationService.isGraduated(studentId, cursusId);
    var statuses = graduationService.getCourseValidationStatuses(studentId, cursusId);
    return ResponseEntity.ok(
        new GraduationStatusResponse(
            studentId,
            student.getFirstName() + " " + student.getLastName(),
            cursusId,
            graduated,
            statuses));
  }

  @GetMapping("/students/{studentId}/statuses")
  public ResponseEntity<List<CourseValidationStatus>> getStatuses(@PathVariable UUID studentId) {
    var active =
        groupMembershipService
            .findActiveByStudent(studentId)
            .orElseThrow(() -> new IllegalStateException("Student has no active group"));
    var cursus =
        active.group().cursus().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No cursus found"));
    var cursusId = cursus.id();
    var statuses = graduationService.getCourseValidationStatuses(studentId, cursusId);
    return ResponseEntity.ok(statuses);
  }

  @GetMapping("/students/{studentId}/courses/{courseId}/details")
  public ResponseEntity<List<ExamScoreDetail>> getExamDetails(
      @PathVariable UUID studentId, @PathVariable UUID courseId) {
    var details = graduationService.getExamScoreDetails(studentId, courseId);
    return ResponseEntity.ok(details);
  }

  @GetMapping("/cursus/{cursusId}/summary")
  public ResponseEntity<GraduationSummaryResponse> getSummary(@PathVariable UUID cursusId) {
    var activeStudents = groupMembershipService.getActiveStudentsByCursusId(cursusId);
    long graduatedCount =
        activeStudents.stream()
            .filter(s -> graduationService.isGraduated(s.getId(), cursusId))
            .count();
    double pct =
        activeStudents.isEmpty() ? 0 : (double) graduatedCount / activeStudents.size() * 100;
    return ResponseEntity.ok(
        new GraduationSummaryResponse(cursusId, activeStudents.size(), graduatedCount, pct));
  }
}
