package hei.school.app.security.authorizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import hei.school.app.model.User;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.GroupMembershipRepository;
import hei.school.app.security.model.Principal;
import hei.school.app.security.model.UserRole;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@ExtendWith(MockitoExtension.class)
class CourseAccessAuthorizationManagerTest {
  @Mock private CourseRepository courseRepository;

  @Mock private GroupMembershipRepository groupMembershipRepository;

  @InjectMocks private CourseAccessAuthorizationManager manager;

  private Principal adminPrincipal;
  private Principal teacherPrincipal;
  private Principal studentPrincipal;
  private Supplier<Authentication> authenticationSupplier;

  @BeforeEach
  void setUp() {
    var adminUser =
        User.builder().id(UUID.randomUUID()).email("admin@test.com").role(UserRole.ADMIN).build();
    adminPrincipal = new Principal(adminUser);

    var teacherUser =
        User.builder()
            .id(UUID.randomUUID())
            .email("teacher@test.com")
            .role(UserRole.TEACHER)
            .build();
    teacherPrincipal = new Principal(teacherUser);

    var studentUser =
        User.builder()
            .id(UUID.randomUUID())
            .email("student@test.com")
            .role(UserRole.STUDENT)
            .build();
    studentPrincipal = new Principal(studentUser);
  }

  @Test
  void shouldAllowAdminAccess() {
    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(adminPrincipal);
    when(auth.isAuthenticated()).thenReturn(true);
    authenticationSupplier = () -> auth;

    var context = mock(RequestAuthorizationContext.class);
    var variables = new HashMap<String, String>();
    variables.put("courseId", UUID.randomUUID().toString());

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isTrue();
    verifyNoInteractions(courseRepository);
  }

  @Test
  void shouldAllowTeacherAccessIfTeacherOwnsCourse() {
    UUID courseId = UUID.randomUUID();
    UUID teacherId = teacherPrincipal.user().id();

    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(teacherPrincipal);
    when(auth.isAuthenticated()).thenReturn(true);
    authenticationSupplier = () -> auth;

    var context = mock(RequestAuthorizationContext.class);
    var variables = new HashMap<String, String>();
    variables.put("courseId", courseId.toString());
    when(context.getVariables()).thenReturn(variables);

    when(courseRepository.existsByIdAndTeacherId(courseId, teacherId)).thenReturn(true);

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isTrue();
    verify(courseRepository).existsByIdAndTeacherId(courseId, teacherId);
  }

  @Test
  void shouldDenyTeacherAccessIfTeacherDoesNotOwnCourse() {
    UUID courseId = UUID.randomUUID();
    UUID teacherId = teacherPrincipal.user().id();

    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(teacherPrincipal);
    when(auth.isAuthenticated()).thenReturn(true);
    authenticationSupplier = () -> auth;

    var context = mock(RequestAuthorizationContext.class);
    var variables = new HashMap<String, String>();
    variables.put("courseId", courseId.toString());
    when(context.getVariables()).thenReturn(variables);

    when(courseRepository.existsByIdAndTeacherId(courseId, teacherId)).thenReturn(false);

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isFalse();
  }

  @Test
  void shouldAllowStudentAccessIfEnrolledInCursus() {
    UUID courseId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    UUID studentId = studentPrincipal.user().id();

    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(studentPrincipal);
    when(auth.isAuthenticated()).thenReturn(true);
    authenticationSupplier = () -> auth;

    var context = mock(RequestAuthorizationContext.class);
    var variables = new HashMap<String, String>();
    variables.put("courseId", courseId.toString());
    when(context.getVariables()).thenReturn(variables);

    when(courseRepository.findCursusIdByCourseId(courseId)).thenReturn(Optional.of(cursusId));
    when(groupMembershipRepository.existsByStudentIdAndCursusId(studentId, cursusId))
        .thenReturn(true);

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isTrue();
  }

  @Test
  void shouldDenyStudentAccessIfNotEnrolledInCursus() {
    UUID courseId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    UUID studentId = studentPrincipal.user().id();

    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(studentPrincipal);
    when(auth.isAuthenticated()).thenReturn(true);
    authenticationSupplier = () -> auth;

    var context = mock(RequestAuthorizationContext.class);
    var variables = new HashMap<String, String>();
    variables.put("courseId", courseId.toString());
    when(context.getVariables()).thenReturn(variables);

    when(courseRepository.findCursusIdByCourseId(courseId)).thenReturn(Optional.of(cursusId));
    when(groupMembershipRepository.existsByStudentIdAndCursusId(studentId, cursusId))
        .thenReturn(false);

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isFalse();
  }

  @Test
  void shouldDenyWhenCourseIdPathVariableIsMissing() {
    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(teacherPrincipal);
    when(auth.isAuthenticated()).thenReturn(true);
    authenticationSupplier = () -> auth;

    var context = mock(RequestAuthorizationContext.class);
    var variables = new HashMap<String, String>(); // Empty -> no courseId
    when(context.getVariables()).thenReturn(variables);

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isFalse();
    verifyNoInteractions(courseRepository);
  }

  @Test
  void shouldDenyWhenAuthenticationIsNull() {
    authenticationSupplier = () -> null;
    var context = mock(RequestAuthorizationContext.class);

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isFalse();
  }

  @Test
  void shouldDenyWhenPrincipalIsNotInstanceOfPrincipal() {
    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn("SomeString");
    when(auth.isAuthenticated()).thenReturn(true);
    authenticationSupplier = () -> auth;

    var context = mock(RequestAuthorizationContext.class);

    AuthorizationDecision decision = manager.check(authenticationSupplier, context);

    assertThat(decision.isGranted()).isFalse();
  }
}
