package hei.school.app.security.authorizer;

import static hei.school.app.security.model.UserRole.ADMIN;
import static hei.school.app.security.model.UserRole.STUDENT;
import static hei.school.app.security.model.UserRole.TEACHER;

import hei.school.app.repository.GroupMembershipRepository;
import hei.school.app.security.model.Principal;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentAccessAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {
  private static final String PATH_VARIABLE = "studentId";

  private final GroupMembershipRepository groupMembershipRepository;

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
    var authentication = authenticationSupplier.get();
    if (authentication == null
        || !authentication.isAuthenticated()
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
    var studentId = UUID.fromString(raw);

    if (STUDENT.equals(role)) {
      return new AuthorizationDecision(principal.user().id().equals(studentId));
    }

    if (TEACHER.equals(role)) {
      return new AuthorizationDecision(
          groupMembershipRepository.existsTeacherAccessToStudent(principal.user().id(), studentId));
    }

    return new AuthorizationDecision(false);
  }
}
