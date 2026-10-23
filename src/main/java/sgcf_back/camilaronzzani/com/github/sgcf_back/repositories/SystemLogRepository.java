package sgcf_back.camilaronzzani.com.github.sgcf_back.repositories;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    List<SystemLog> findAllByOrderByCreatedAtDesc();

    List<SystemLog> findByLevelOrderByCreatedAtDesc(String level);

    long countByLevel(String level);
}
