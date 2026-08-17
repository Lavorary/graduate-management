package hei.school.app.repository;

import hei.school.app.repository.model.JGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<JGroup, UUID> {
    Optional<JGroup> findByRef(String ref);
    List<JGroup> findBuCursus_id(UUID cursusId);

}
