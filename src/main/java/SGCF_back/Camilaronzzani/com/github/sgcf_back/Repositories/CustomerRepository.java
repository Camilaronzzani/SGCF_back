package SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer , Long> {
    List<Customer> findByActiveTrue();
}
