package sgcf_back.camilaronzzani.com.github.sgcf_back.entityTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerTest {

    @Test
    void prePersistCreatesEmptyLists() {
        Customer customer = new Customer();

        ReflectionTestUtils.invokeMethod(customer, "prePersist");

        assertTrue(customer.getLanguageSpeak().isEmpty());
        assertTrue(customer.getReservations().isEmpty());
    }
}