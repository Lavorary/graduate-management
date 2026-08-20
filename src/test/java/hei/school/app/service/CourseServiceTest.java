package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.DTOs.CourseDTO;
import hei.school.app.mapper.CourseMapper;
import hei.school.app.model.Course;
import hei.school.app.model.Cursus;
import hei.school.app.model.User;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock private CourseRepository courseRepository;
  @Mock private CursusRepository cursusRepository;
  @Mock private UserRepository userRepository;
  @Mock private CourseMapper courseMapper;
  @InjectMocks private CourseService courseService;

  private JCursus jCursus(UUID id) {
    return JCursus.builder()
        .id(id)
        .name("DevLog")
        .description("Development Logic")
        .year("2026")
        .build();
  }

  private JUser jTeacher(UUID id) {
    return JUser.builder()
        .id(id)
        .firstName("Marie")
        .lastName("T.")
        .role(UserRole.TEACHER)
        .email("marie@hei.school")
        .password("encoded")
        .build();
  }

  private Cursus cursusModel(UUID id) {
    return new Cursus(id, "DevLog", "Development Logic", "2026");
  }

  private User userModel(UUID id) {
    return new User(id, "Marie", "T.", UserRole.TEACHER, "marie@hei.school", "encoded");
  }

  @Test
  void should_create_course_with_teachers() {
    UUID cursusId = UUID.randomUUID();
    UUID teacherId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();

    JCursus jCursus = jCursus(cursusId);
    JUser jTeacher = jTeacher(teacherId);
    JCourse savedEntity =
        JCourse.builder()
            .id(courseId)
            .cursus(jCursus)
            .ref("ALG101")
            .title("Algorithmique")
            .credit(5)
            .teachers(Set.of(jTeacher))
            .build();

    Cursus cursus = cursusModel(cursusId);
    User teacher = userModel(teacherId);
    Course model = new Course(courseId, cursus, "ALG101", "Algorithmique", 5, Set.of(teacher));

    when(cursusRepository.findById(cursusId)).thenReturn(Optional.of(jCursus));
    when(userRepository.findById(teacherId)).thenReturn(Optional.of(jTeacher));
    when(courseRepository.save(any(JCourse.class))).thenReturn(savedEntity);
    when(courseMapper.toModel(savedEntity)).thenReturn(model);

    CourseDTO result =
        courseService.create(cursusId, "ALG101", "Algorithmique", 5, Set.of(teacherId));

    ArgumentCaptor<JCourse> captor = ArgumentCaptor.forClass(JCourse.class);
    verify(courseRepository).save(captor.capture());
    assertThat(captor.getValue().getRef()).isEqualTo("ALG101");
    assertThat(captor.getValue().getCredit()).isEqualTo(5);
    assertThat(captor.getValue().getTeachers()).containsExactly(jTeacher);

    assertThat(result.ref()).isEqualTo("ALG101");
    assertThat(result.cursusId()).isEqualTo(cursusId);
    assertThat(result.teacherIds()).containsExactly(teacherId);
  }

  @Test
  void should_create_course_without_teachers() {
    UUID cursusId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();

    JCursus jCursus = jCursus(cursusId);
    JCourse savedEntity =
        JCourse.builder()
            .id(courseId)
            .cursus(jCursus)
            .ref("ALG101")
            .title("Algorithmique")
            .credit(5)
            .teachers(Set.of())
            .build();

    Cursus cursus = cursusModel(cursusId);
    Course model = new Course(courseId, cursus, "ALG101", "Algorithmique", 5, Set.of());

    when(cursusRepository.findById(cursusId)).thenReturn(Optional.of(jCursus));
    when(courseRepository.save(any(JCourse.class))).thenReturn(savedEntity);
    when(courseMapper.toModel(savedEntity)).thenReturn(model);

    CourseDTO result = courseService.create(cursusId, "ALG101", "Algorithmique", 5, null);

    assertThat(result.teacherIds()).isEmpty();
    verify(userRepository, never()).findById(any());
  }

  @Test
  void should_throw_when_cursus_not_found_on_create() {
    UUID cursusId = UUID.randomUUID();
    when(cursusRepository.findById(cursusId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> courseService.create(cursusId, "ALG101", "Algo", 5, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(cursusId.toString());
  }

  @Test
  void should_throw_when_teacher_not_found_on_create() {
    UUID cursusId = UUID.randomUUID();
    UUID teacherId = UUID.randomUUID();

    when(cursusRepository.findById(cursusId)).thenReturn(Optional.of(jCursus(cursusId)));
    when(userRepository.findById(teacherId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> courseService.create(cursusId, "ALG101", "Algo", 5, Set.of(teacherId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(teacherId.toString());
  }

  @Test
  void should_get_course_by_id() {
    UUID courseId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();

    JCursus jCursus = jCursus(cursusId);
    JCourse entity =
        JCourse.builder()
            .id(courseId)
            .cursus(jCursus)
            .ref("ALG101")
            .title("Algo")
            .credit(5)
            .build();

    Course model = new Course(courseId, cursusModel(cursusId), "ALG101", "Algo", 5, Set.of());

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(entity));
    when(courseMapper.toModel(entity)).thenReturn(model);

    CourseDTO result = courseService.getById(courseId);
    assertThat(result.id()).isEqualTo(courseId);
    assertThat(result.ref()).isEqualTo("ALG101");
  }

  @Test
  void should_throw_when_course_not_found_by_id() {
    UUID courseId = UUID.randomUUID();
    when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> courseService.getById(courseId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(courseId.toString());
  }

  @Test
  void should_find_by_cursus() {
    UUID cursusId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();

    JCursus jCursus = jCursus(cursusId);
    JCourse entity =
        JCourse.builder()
            .id(courseId)
            .cursus(jCursus)
            .ref("ALG101")
            .title("Algo")
            .credit(5)
            .build();

    Course model = new Course(courseId, cursusModel(cursusId), "ALG101", "Algo", 5, Set.of());

    when(courseRepository.findByCursusId(cursusId)).thenReturn(List.of(entity));
    when(courseMapper.toModel(entity)).thenReturn(model);

    List<CourseDTO> results = courseService.findByCursus(cursusId);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().cursusId()).isEqualTo(cursusId);
  }

  @Test
  void should_return_empty_list_when_no_courses_found_by_cursus() {
    UUID cursusId = UUID.randomUUID();
    when(courseRepository.findByCursusId(cursusId)).thenReturn(List.of());

    assertThat(courseService.findByCursus(cursusId)).isEmpty();
  }

  @Test
  void should_find_by_teacher() {
    UUID teacherId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();

    JUser jTeacher = jTeacher(teacherId);
    JCursus jCursus = jCursus(cursusId);
    JCourse entity =
        JCourse.builder()
            .id(courseId)
            .cursus(jCursus)
            .ref("ALG101")
            .title("Algo")
            .credit(5)
            .teachers(Set.of(jTeacher))
            .build();

    User teacher = userModel(teacherId);
    Course model =
        new Course(courseId, cursusModel(cursusId), "ALG101", "Algo", 5, Set.of(teacher));

    when(courseRepository.findByTeachers_Id(teacherId)).thenReturn(List.of(entity));
    when(courseMapper.toModel(entity)).thenReturn(model);

    List<CourseDTO> results = courseService.findByTeacher(teacherId);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().teacherIds()).contains(teacherId);
  }

  @Test
  void should_return_empty_list_when_no_courses_found_by_teacher() {
    UUID teacherId = UUID.randomUUID();
    when(courseRepository.findByTeachers_Id(teacherId)).thenReturn(List.of());

    assertThat(courseService.findByTeacher(teacherId)).isEmpty();
  }

  @Test
  void should_find_by_cursus_ids() {
    UUID cursusId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();

    JCursus jCursus = jCursus(cursusId);
    JCourse entity =
        JCourse.builder()
            .id(courseId)
            .cursus(jCursus)
            .ref("ALG101")
            .title("Algo")
            .credit(5)
            .build();
    Course model = new Course(courseId, cursusModel(cursusId), "ALG101", "Algo", 5, Set.of());

    when(courseRepository.findAllByCursusIdIn(List.of(cursusId))).thenReturn(List.of(entity));
    when(courseMapper.toModel(entity)).thenReturn(model);

    List<CourseDTO> results = courseService.findByCursusIds(List.of(cursusId));
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().cursusId()).isEqualTo(cursusId);
  }

  @Test
  void should_return_empty_list_when_no_courses_found_by_cursus_ids() {
    UUID cursusId = UUID.randomUUID();
    when(courseRepository.findAllByCursusIdIn(List.of(cursusId))).thenReturn(List.of());

    assertThat(courseService.findByCursusIds(List.of(cursusId))).isEmpty();
  }

  @Test
  void should_return_true_when_course_is_taught_by_teacher() {
    UUID courseId = UUID.randomUUID();
    UUID teacherId = UUID.randomUUID();
    when(courseRepository.existsByIdAndTeacherId(courseId, teacherId)).thenReturn(true);

    assertThat(courseService.isTaughtBy(courseId, teacherId)).isTrue();
  }

  @Test
  void should_return_false_when_course_is_not_taught_by_teacher() {
    UUID courseId = UUID.randomUUID();
    UUID teacherId = UUID.randomUUID();
    when(courseRepository.existsByIdAndTeacherId(courseId, teacherId)).thenReturn(false);

    assertThat(courseService.isTaughtBy(courseId, teacherId)).isFalse();
  }

  @Test
  void should_get_cursus_id_of_course() {
    UUID courseId = UUID.randomUUID();
    UUID cursusId = UUID.randomUUID();
    when(courseRepository.findCursusIdByCourseId(courseId)).thenReturn(Optional.of(cursusId));

    assertThat(courseService.getCursusIdOf(courseId)).isEqualTo(cursusId);
  }

  @Test
  void should_throw_when_getting_cursus_id_of_unknown_course() {
    UUID courseId = UUID.randomUUID();
    when(courseRepository.findCursusIdByCourseId(courseId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> courseService.getCursusIdOf(courseId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(courseId.toString());
  }
}
