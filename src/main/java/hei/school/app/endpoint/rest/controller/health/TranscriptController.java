package hei.school.app.endpoint.rest.controller.health;

import hei.school.app.DTOs.UserDTO;
import hei.school.app.endpoint.event.EventProducer;
import hei.school.app.endpoint.event.model.SendTranscriptRequested;
import hei.school.app.security.model.UserRole;
import hei.school.app.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transcripts")
@AllArgsConstructor
public class TranscriptController {
    private final UserService userService;
    private final EventProducer<SendTranscriptRequested> eventProducer;

    @PostMapping("/{studentId}")
    @SneakyThrows
    public ResponseEntity<Void> sendTranscript(
            @PathVariable UUID studentId,
            Authentication authentication) {

        UserDTO requester = userService.getByEmail(authentication.getName());

        boolean isSelf = requester.id().equals(studentId);
        boolean isAdmin = requester.role() == UserRole.ADMIN;

        if (!isSelf && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to request this transcript");
        }

        try {
            userService.getById(studentId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Student with Id : " + studentId + " not found");
        }


        var event = SendTranscriptRequested.builder()
            .studentId(studentId)
            .build();

        eventProducer.accept(List.of(event));

        return ResponseEntity.accepted().build();
    }
}