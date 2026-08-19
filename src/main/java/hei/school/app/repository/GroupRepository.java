package hei.school.app.repository;

import hei.school.app.repository.model.JGroup;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<JGroup, UUID> {
  Optional<JGroup> findByRef(String ref);
  List<JGroup> findByCursus_Id(UUID cursusId);
}
