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
public class GradeAccessAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static final String PATH_VARIABLE = "gradeId";

  private final JGradeRepository gradeRepository;
  private final JCourseRepository courseRepository;

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
    var gradeId = UUID.fromString(raw);

    if (STUDENT.equals(role)) {
      return new AuthorizationDecision(
          gradeRepository.existsByIdAndStudentId(gradeId, principal.user().id()));
    }

    if (TEACHER.equals(role)) {
      var courseId = gradeRepository.findCourseIdByGradeId(gradeId).orElse(null);
      if (courseId == null) {
        return new AuthorizationDecision(false);
      }
      return new AuthorizationDecision(
          courseRepository.existsByIdAndTeacherId(courseId, principal.user().id()));
    }

    return new AuthorizationDecision(false);
  }
}
