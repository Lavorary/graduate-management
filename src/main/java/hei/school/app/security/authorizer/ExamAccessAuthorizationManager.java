package hei.school.app.security.authorizer;

import java.util.UUID;
import java.util.function.Supplier;
import static hei.school.app.security.model.UserRole.ADMIN;
import static hei.school.app.security.model.UserRole.STUDENT;
import static hei.school.app.security.model.UserRole.TEACHER;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import hei.school.app.security.model.Principal;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExamAccessAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static final String PATH_VARIABLE = "examId";

  private final JExamRepository examRepository;
  private final JCourseRepository courseRepository;
  private final JGroupMembershipRepository groupMembershipRepository;

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
    var authentication = authenticationSupplier.get();
    if (authentication == null || !authentication.isAuthenticated()
        || !(authentication.getPrincipal() instanceof Principal principal)) {
      return new AuthorizationDecision(false);
    }

    var role = principal.user().role();
    if (ADMIN.equals(role)) {
      return new AuthorizationDecision(true);
    }

    var raw = context.getVariables().get(PATH_VARIABLE);
    if (raw == null) {
      return new AuthorizationDecision(false);
    }
    var examId = UUID.fromString(raw);

    if (TEACHER.equals(role)) {
      var courseId = examRepository.findCourseIdByExamId(examId).orElse(null);
      if (courseId == null) {
        return new AuthorizationDecision(false);
      }
      return new AuthorizationDecision(
          courseRepository.existsByIdAndTeacherId(courseId, principal.user().id()));
    }

    if (STUDENT.equals(role)) {
      var cursusId = examRepository.findCursusIdByExamId(examId).orElse(null);
      if (cursusId == null) {
        return new AuthorizationDecision(false);
      }
      return new AuthorizationDecision(
          groupMembershipRepository.existsByStudentIdAndCursusId(principal.user().id(), cursusId));
    }

    return new AuthorizationDecision(false);
  }
}
