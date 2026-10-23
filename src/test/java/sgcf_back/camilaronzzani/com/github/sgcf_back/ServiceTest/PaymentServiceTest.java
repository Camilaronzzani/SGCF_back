package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PaymentRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Payment;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.CustomerRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.PaymentRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.PaymentService;

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
        // Given
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findAll()).thenReturn(payments);

        // When
        List<Payment> result = paymentService.findAll();

        // Then
        assertSame(payments, result);
    }

    @Test
    void findByIdReturnsPaymentWhenItExists() {
        // Given
        Payment payment = new Payment();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When
        Payment result = paymentService.findById(1L);

        // Then
        assertSame(payment, result);
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenPaymentDoesNotExist() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentService.findById(1L));

        // Then
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void toPaymentMapsRequestAndDefaultsStatusToPending() {
        // Given
        Customer customer = new Customer();
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, null, 125.50);

        // When
        Payment payment = paymentService.toPayment(request);

        // Then
        assertSame(customer, payment.getCustomer());
        assertEquals(125.50, payment.getTotalAccount());
        assertEquals(Status.Pending, payment.getStatus());
        assertTrue(payment.isActive());
    }

    @Test
    void toPaymentUsesStatusProvidedInRequest() {
        // Given
        Customer customer = new Customer();
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, Status.Confirmed, 125.50);

        // When
        Payment payment = paymentService.toPayment(request);

        // Then
        assertEquals(Status.Confirmed, payment.getStatus());
    }

    @Test
    void savePersistsMappedPayment() {
        // Given
        Customer customer = new Customer();
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, Status.Pending, 125.50);

        // When
        paymentService.save(request);

        // Then
        verify(paymentRepository).save(argThat(payment ->
                payment.getCustomer() == customer
                        && payment.getTotalAccount() == 125.50
                        && payment.getStatus() == Status.Pending
                        && payment.isActive()));
    }

    @Test
    void updateChangesPaymentFieldsAndReturnsUpdatedPayment() {
        // Given
        Payment existing = new Payment();
        Customer customer = new Customer();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        PaymentRequest request = new PaymentRequest(7L, Status.Confirmed, 200.0);

        // When
        Payment updated = paymentService.update(request, 1L);

        // Then
        assertSame(existing, updated);
        assertSame(customer, updated.getCustomer());
        assertEquals(200.0, updated.getTotalAccount());
        assertEquals(Status.Confirmed, updated.getStatus());
    }

    @Test
    void deleteMarksPaymentInactiveWithoutDeletingIt() {
        // Given
        Payment payment = new Payment();
        payment.setActive(true);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When
        paymentService.delete(1L);

        // Then
        assertFalse(payment.isActive());
        verify(paymentRepository, never()).delete(any());
    }

    @Test
    void findAllActiveReturnsActivePaymentsFromRepository() {
        // Given
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findByActiveTrue()).thenReturn(payments);

        // When
        List<Payment> result = paymentService.findAllActive();

        // Then
        assertSame(payments, result);
    }

    @Test
    void findByCustomerDelegatesToRepository() {
        // Given
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findByCustomerId(7L)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.findByCustomer(7L);

        // Then
        assertSame(payments, result);
    }

    @Test
    void findByStatusDelegatesToRepository() {
        // Given
        List<Payment> payments = List.of(new Payment());
        when(paymentRepository.findByStatus(Status.Confirmed)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.findByStatus(Status.Confirmed);

        // Then
        assertSame(payments, result);
    }
}
