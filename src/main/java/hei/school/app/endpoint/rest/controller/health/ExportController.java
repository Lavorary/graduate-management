package hei.school.app.endpoint.rest.controller.health;

import hei.school.app.DTOs.UserDTO;
import hei.school.app.security.model.UserRole;
import hei.school.app.service.ExportService;
import hei.school.app.service.UserService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotions")
@AllArgsConstructor
public class ExportController {

    private final UserService userService;
    private final ExportService exportService;

    @GetMapping("/{promotionId}/graduates/export")
    public ResponseEntity<byte[]> exportGraduates(
            @PathVariable UUID promotionId,
            Authentication authentication) {

        UserDTO requester = userService.getByEmail(authentication.getName());

        if (requester.role() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only admins can export graduates");
        }

        byte[] excelData = exportService.generateExcel(promotionId);

        String filename = "graduates-" + promotionId + ".xlsx";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(excelData);
    }
}