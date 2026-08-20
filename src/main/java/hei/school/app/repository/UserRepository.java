package hei.school.app.repository;

import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<JUser, UUID> {
  Optional<JUser> findByEmail(String email);

  List<JUser> findByRole(UserRole role);

  @Query("SELECT DISTINCT u FROM JUser u " +
         "JOIN JGrade g ON g.student.id = u.id " +
         "JOIN JExam e ON g.exam.id = e.id " +
         "WHERE e.course.cursus.id = :cursusId")
  List<JUser> findGraduatesByCursusId(@Param("cursusId") UUID cursusId);
}
