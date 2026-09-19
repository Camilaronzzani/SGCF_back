package SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    Optional<PasswordReset> findFirstByUserIdAndUsedFalseOrderByExpirationDesc(
            Long userId
    );}
