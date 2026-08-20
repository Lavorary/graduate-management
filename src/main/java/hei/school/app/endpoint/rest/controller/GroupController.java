package hei.school.app.endpoint.rest.controller;

import hei.school.app.DTOs.GroupDTO;
import hei.school.app.dto.CreateGroupRequest;
import hei.school.app.service.GroupService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
  private final GroupService groupService;

  @GetMapping
  public ResponseEntity<List<GroupDTO>> getByCursus(@RequestParam UUID cursusId) {
    var groups = groupService.findByCursus(cursusId);
    return ResponseEntity.ok(groups);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GroupDTO> get(@PathVariable UUID id) {
    var group = groupService.getById(id);
    return ResponseEntity.ok(group);
  }

  @PostMapping
  public ResponseEntity<GroupDTO> create(@Valid @RequestBody CreateGroupRequest request) {
    var group = groupService.create(request.ref(), request.cursusIds());
    return ResponseEntity.status(HttpStatus.CREATED).body(group);
  }
}
