package sgcf_back.camilaronzzani.com.github.sgcf_back.repositoryTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer newCustomer(String name, String email, boolean active) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setActive(active);
        return customer;
    }

    @Test
    @DisplayName("Buscar só os clientes ativos")
    void findByActiveTrueReturnsOnlyActiveCustomers() {
        entityManager.persist(newCustomer("Camila", "camila@email.com", true));
        entityManager.persist(newCustomer("Alex", "alex@email.com", false));

        List<Customer> result = customerRepository.findByActiveTrue();

        assertEquals(1, result.size());
        assertEquals("Camila", result.get(0).getName());
    }

    @Test
    @DisplayName("Não retornar nada quando não há clientes ativos")
    void findByActiveTrueReturnsEmptyListWhenNoCustomerIsActive() {
        entityManager.persist(newCustomer("Alex", "alex@email.com", false));

        List<Customer> result = customerRepository.findByActiveTrue();

        assertEquals(0, result.size());
    }
}
