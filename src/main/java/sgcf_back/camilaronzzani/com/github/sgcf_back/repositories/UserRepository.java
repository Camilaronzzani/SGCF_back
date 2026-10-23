package sgcf_back.camilaronzzani.com.github.sgcf_back.repositories;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByActiveTrue();

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(Long employeeId);

}
