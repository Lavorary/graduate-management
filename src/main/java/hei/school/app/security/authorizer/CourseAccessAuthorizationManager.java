package hei.school.app.security.authorizer;

import static hei.school.app.security.model.UserRole.ADMIN;
import static hei.school.app.security.model.UserRole.STUDENT;
import static hei.school.app.security.model.UserRole.TEACHER;

import hei.school.app.repository.CourseRepository;
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
public class CourseAccessAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {
  private static final String PATH_VARIABLE = "courseId";

  private final CourseRepository courseRepository;
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
    var courseId = UUID.fromString(raw);

    if (TEACHER.equals(role)) {
      return new AuthorizationDecision(
          courseRepository.existsByIdAndTeacherId(courseId, principal.user().id()));
    }

    if (STUDENT.equals(role)) {
      var cursusId = courseRepository.findCursusIdByCourseId(courseId).orElse(null);
      if (cursusId == null) {
        return new AuthorizationDecision(false);
      }
      return new AuthorizationDecision(
          groupMembershipRepository.existsByStudentIdAndCursusId(principal.user().id(), cursusId));
    }

    return new AuthorizationDecision(false);
  }
}
