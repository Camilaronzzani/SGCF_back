package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PaymentRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Payment;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.CustomerRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.PaymentRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void findAllReturnsPaymentsFromRepository() {
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findAll()).thenReturn(payments);

        assertSame(payments, paymentService.findAll());
    }

    @Test
    void findByIdReturnsPaymentWhenItExists() {
        Payment payment = new Payment();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertSame(payment, paymentService.findById(1L));
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenPaymentDoesNotExist() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentService.findById(1L));

        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void toPaymentMapsRequestAndDefaultsStatusToPending() {
        Customer customer = new Customer();
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, null, 125.50);

        Payment payment = paymentService.toPayment(request);

        assertSame(customer, payment.getCustomer());
        assertEquals(125.50, payment.getTotalAccount());
        assertEquals(Status.Pending, payment.getStatus());
        assertTrue(payment.isActive());
    }

    @Test
    void toPaymentUsesStatusProvidedInRequest() {
        Customer customer = new Customer();
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, Status.Confirmed, 125.50);

        assertEquals(Status.Confirmed, paymentService.toPayment(request).getStatus());
    }

    @Test
    void savePersistsMappedPayment() {
        Customer customer = new Customer();
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, Status.Pending, 125.50);

        paymentService.save(request);

        verify(paymentRepository).save(argThat(payment ->
                payment.getCustomer() == customer
                        && payment.getTotalAccount() == 125.50
                        && payment.getStatus() == Status.Pending
                        && payment.isActive()));
    }

    @Test
    void updateChangesPaymentFieldsAndReturnsUpdatedPayment() {
        Payment existing = new Payment();
        Customer customer = new Customer();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, Status.Confirmed, 200.0);

        Payment updated = paymentService.update(request, 1L);

        assertSame(existing, updated);
        assertSame(customer, updated.getCustomer());
        assertEquals(200.0, updated.getTotalAccount());
        assertEquals(Status.Confirmed, updated.getStatus());
    }

    @Test
    void deleteMarksPaymentInactiveWithoutDeletingIt() {
        Payment payment = new Payment();
        payment.setActive(true);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.delete(1L);

        assertFalse(payment.isActive());
        verify(paymentRepository, never()).delete(any());
    }

    @Test
    void findAllActiveReturnsActivePaymentsFromRepository() {
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findByActiveTrue()).thenReturn(payments);

        assertSame(payments, paymentService.findAllActive());
    }

    @Test
    void findByCustomerDelegatesToRepository() {
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findByCustomerId(7L)).thenReturn(payments);

        assertSame(payments, paymentService.findByCustomer(7L));
    }

    @Test
    void findByStatusDelegatesToRepository() {
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findByStatus(Status.Confirmed)).thenReturn(payments);

        assertSame(payments, paymentService.findByStatus(Status.Confirmed));
    }
}
