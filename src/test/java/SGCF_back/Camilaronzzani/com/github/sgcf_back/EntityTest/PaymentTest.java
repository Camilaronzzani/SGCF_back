package sgcf_back.camilaronzzani.com.github.sgcf_back.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {

    @Test
    void prePersistSetsPendingStatusWhenNull() {
        Payment payment = new Payment();

        ReflectionTestUtils.invokeMethod(payment, "prePersist");

        assertEquals(Status.Pending, payment.getStatus());
    }

    @Test
    void prePersistKeepsExistingStatus() {
        Payment payment = new Payment();
        payment.setStatus(Status.Confirmed);

        ReflectionTestUtils.invokeMethod(payment, "prePersist");

        assertEquals(Status.Confirmed, payment.getStatus());
    }
}