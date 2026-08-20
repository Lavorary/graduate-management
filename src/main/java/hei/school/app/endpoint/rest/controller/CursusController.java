package hei.school.app.endpoint.rest.controller;

import hei.school.app.DTOs.CursusDTO;
import hei.school.app.dto.CreateCursusRequest;
import hei.school.app.service.CursusService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursus")
@RequiredArgsConstructor
public class CursusController {
  private final CursusService cursusService;

  @GetMapping
  public ResponseEntity<List<CursusDTO>> getAll() {
    var list = cursusService.findAll();
    return ResponseEntity.ok(list);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CursusDTO> get(@PathVariable UUID id) {
    var cursus = cursusService.getById(id);
    return ResponseEntity.ok(cursus);
  }

  @PostMapping
  public ResponseEntity<CursusDTO> create(@Valid @RequestBody CreateCursusRequest request) {
    var cursus = cursusService.create(request.name(), request.description(), request.year());
    return ResponseEntity.status(HttpStatus.CREATED).body(cursus);
  }
}
