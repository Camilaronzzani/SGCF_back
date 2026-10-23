package sgcf_back.camilaronzzani.com.github.sgcf_back.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationTest {

    @Test
    void prePersistSetsPendingStatusWhenNull() {
        Reservation reservation = new Reservation();

        ReflectionTestUtils.invokeMethod(reservation, "prePersist");

        assertEquals(Status.Pending, reservation.getStatus());
    }

    @Test
    void prePersistKeepsExistingStatus() {
        Reservation reservation = new Reservation();
        reservation.setStatus(Status.Confirmed);

        ReflectionTestUtils.invokeMethod(reservation, "prePersist");

        assertEquals(Status.Confirmed, reservation.getStatus());
    }

    @Test
    void prePersistCreatesEmptyCustomersNotPayingList() {
        Reservation reservation = new Reservation();

        ReflectionTestUtils.invokeMethod(reservation, "prePersist");

        assertTrue(reservation.getCustomersNotPaying().isEmpty());
    }
}