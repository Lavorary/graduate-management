package hei.school.app.service;

import hei.school.app.DTOs.CourseDTO;
import hei.school.app.mapper.CourseMapper;
import hei.school.app.model.Course;
import hei.school.app.model.User;
import hei.school.app.repository.CourseRepository;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JCourse;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JUser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;
  private final CursusRepository cursusRepository;
  private final UserRepository userRepository;
  private final CourseMapper courseMapper;

  public CourseDTO create(
      UUID cursusId, String ref, String title, int credit, Set<UUID> teacherIds) {
    JCursus cursus =
        cursusRepository
            .findById(cursusId)
            .orElseThrow(
                () -> new IllegalArgumentException("Cursus with Id : " + cursusId + "  not found"));

    Set<JUser> teachers = new HashSet<>();
    if (teacherIds != null) {
      for (UUID teacherId : teacherIds) {
        teachers.add(
            userRepository
                .findById(teacherId)
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Teacher with Id : " + teacherId + "  not found")));
      }
    }

    JCourse saved =
        courseRepository.save(
            JCourse.builder()
                .cursus(cursus)
                .ref(ref)
                .title(title)
                .credit(credit)
                .teachers(teachers)
                .build());
    return toDto(saved);
  }

  public CourseDTO getById(UUID id) {
    JCourse entity =
        courseRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Cours during with Id : " + id + " not found"));
    return toDto(entity);
  }

  public List<CourseDTO> findByCursus(UUID cursusId) {
    return courseRepository.findByCursusId(cursusId).stream().map(this::toDto).toList();
  }

  public List<CourseDTO> findByTeacher(UUID teacherId) {
    return courseRepository.findByTeachers_Id(teacherId).stream().map(this::toDto).toList();
  }

  public List<CourseDTO> findByCursusIds(List<UUID> cursusIds) {
    return courseRepository.findAllByCursusIdIn(cursusIds).stream().map(this::toDto).toList();
  }

  public boolean isTaughtBy(UUID courseId, UUID teacherId) {
    return courseRepository.existsByIdAndTeacherId(courseId, teacherId);
  }

  public UUID getCursusIdOf(UUID courseId) {
    return courseRepository
        .findCursusIdByCourseId(courseId)
        .orElseThrow(() -> new IllegalArgumentException("Course with Id : " + courseId + " not found"));
  }

  private CourseDTO toDto(JCourse entity) {
    Course model = courseMapper.toModel(entity);
    Set<UUID> teacherIds =
        model.teachers() == null
            ? Set.of()
            : model.teachers().stream().map(User::id).collect(Collectors.toSet());
    return CourseDTO.builder()
        .id(model.id())
        .cursusId(model.cursus().id())
        .ref(model.ref())
        .title(model.title())
        .credit(model.credit())
        .teacherIds(teacherIds)
        .build();
  }
}