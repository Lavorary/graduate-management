package hei.school.app.repository;

import hei.school.app.repository.model.JCursus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursusRepository extends JpaRepository<JCursus, UUID> {

  List<JCursus> findByYear(String year);
}
