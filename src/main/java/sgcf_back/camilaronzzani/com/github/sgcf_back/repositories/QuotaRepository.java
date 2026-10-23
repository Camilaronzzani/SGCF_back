package sgcf_back.camilaronzzani.com.github.sgcf_back.repositories;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Quota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotaRepository extends JpaRepository<Quota, Long> {
    List<Quota> findByActiveTrue();

    List<Quota> findByEmployeeId(Long employeeId);

    List<Quota> findByEmployeeIsNull();
}
