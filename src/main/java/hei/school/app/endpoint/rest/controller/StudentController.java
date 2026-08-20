package hei.school.app.endpoint.rest.controller;

import hei.school.app.DTOs.GradeDTO;
import hei.school.app.DTOs.GroupDTO;
import hei.school.app.security.model.Principal;
import hei.school.app.service.GradeService;
import hei.school.app.service.GroupMembershipService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class StudentController {
  private final GradeService gradeService;
  private final GroupMembershipService groupMembershipService;

  @GetMapping
  public ResponseEntity<Principal> getCurrentUser(Authentication auth) {
    return ResponseEntity.ok((Principal) auth.getPrincipal());
  }

  @GetMapping("/grades")
  public ResponseEntity<List<GradeDTO>> getMyGrades(Authentication auth) {
    var principal = (Principal) auth.getPrincipal();
    var grades = gradeService.findByStudent(principal.user().id());
    return ResponseEntity.ok(grades);
  }

  @GetMapping("/group")
  public ResponseEntity<GroupDTO> getMyGroup(Authentication auth) {
    var principal = (Principal) auth.getPrincipal();
    var membership =
        groupMembershipService
            .findActiveByStudent(principal.user().id())
            .orElseThrow(() -> new IllegalStateException("No active group"));
    // Map to GroupResponse – left as exercise.
    return ResponseEntity.ok(
        new GroupDTO(membership.group().id(), membership.group().ref(), Set.of()));
  }
}
