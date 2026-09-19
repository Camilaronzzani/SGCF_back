package SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    List<SystemLog> findAllByOrderByCreatedAtDesc();

    List<SystemLog> findByLevelOrderByCreatedAtDesc(String level);

    long countByLevel(String level);
}
