package hei.school.app.repository;

import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<JUser, UUID> {
  Optional<JUser> findByEmail(String email);

  List<JUser> findByRole(UserRole role);
}
