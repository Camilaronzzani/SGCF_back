package SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotaRepository extends JpaRepository<Quota, Long> {
    List<Quota> findByActiveTrue();

    List<Quota> findByEmployeeId(Long employeeId);

    List<Quota> findByEmployeeIsNull();
}
