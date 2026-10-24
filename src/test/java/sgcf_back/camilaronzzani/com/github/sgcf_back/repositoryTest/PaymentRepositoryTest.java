package sgcf_back.camilaronzzani.com.github.sgcf_back.repositoryTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Payment;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    private Customer camila;
    private Customer alex;

    @BeforeEach
    void setUp() {
        camila = new Customer();
        camila.setName("Camila");
        camila.setEmail("camila@email.com");
        entityManager.persist(camila);

        alex = new Customer();
        alex.setName("Alex");
        alex.setEmail("alex@email.com");
        entityManager.persist(alex);
    }

    private Payment newPayment(Customer customer, Status status, boolean active) {
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setStatus(status);
        payment.setTotalAccount(100.0);
        payment.setActive(active);
        return entityManager.persist(payment);
    }

    @Test
    @DisplayName("Buscar pagamentos do cliente")
    void findByCustomerIdReturnsOnlyPaymentsOfThatCustomer() {
        newPayment(camila, Status.PENDING, true);
        newPayment(camila, Status.CONFIRMED, true);
        newPayment(alex, Status.PENDING, true);

        List<Payment> result = paymentRepository.findByCustomerId(camila.getId());

        assertEquals(2, result.size());
        assertEquals("Camila", result.get(0).getCustomer().getName());
    }

    @Test
    @DisplayName("Buscar pagamentos pelo status")
    void findByStatusReturnsOnlyPaymentsWithThatStatus() {
        newPayment(camila, Status.PENDING, true);
        newPayment(alex, Status.CONFIRMED, true);

        List<Payment> result = paymentRepository.findByStatus(Status.CONFIRMED);

        assertEquals(1, result.size());
        assertEquals(Status.CONFIRMED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Buscar só os pagamentos ativos")
    void findByActiveTrueReturnsOnlyActivePayments() {
        newPayment(camila, Status.PENDING, true);
        newPayment(alex, Status.PENDING, false);

        List<Payment> result = paymentRepository.findByActiveTrue();

        assertEquals(1, result.size());
        assertEquals("Camila", result.get(0).getCustomer().getName());
    }

    @Test
    @DisplayName("Salvar pagamento sem status deixa como pendente")
    void saveSetsStatusToPendingWhenStatusIsNull() {
        Payment payment = new Payment();
        payment.setCustomer(camila);
        payment.setTotalAccount(50.0);

        Payment saved = paymentRepository.saveAndFlush(payment);

        assertEquals(Status.PENDING, saved.getStatus());
    }
}
