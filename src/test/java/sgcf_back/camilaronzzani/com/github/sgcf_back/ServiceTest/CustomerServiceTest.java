package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.CustomerRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.CustomerRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService service;

    @Test
    void findAllReturnsAllCustomers() {
        // Given
        Customer c = new Customer();
        when(customerRepository.findAll()).thenReturn(List.of(c));

        // When
        List<Customer> resultado = service.findAll();

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void findByIdReturnsCustomerWhenItExists() {
        // Given
        Customer c = new Customer();
        c.setName("Maria");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        // When
        Customer resultado = service.findById(1L);

        // Then
        assertEquals("Maria", resultado.getName());
    }

    @Test
    void findByIdThrowsWhenCustomerDoesNotExist() {
        // Given
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(99L));

        // Then
        assertTrue(ex.getCause() instanceof ResponseStatusException);
    }

    @Test
    void saveStoresCustomerInRepository() {
        // Given
        CustomerRequest request = mock(CustomerRequest.class);

        // When
        service.save(request);

        // Then
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateChangesCustomerData() {
        // Given
        Customer c = new Customer();
        c.setName("Antigo");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        CustomerRequest request = mock(CustomerRequest.class);
        when(request.name()).thenReturn("Novo");

        // When
        Customer resultado = service.update(request, 1L);

        // Then
        assertEquals("Novo", resultado.getName());
    }

    @Test
    void deleteDeactivatesCustomer() {
        // Given
        Customer c = new Customer();
        c.setActive(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        // When
        service.delete(1L);

        // Then
        assertFalse(c.isActive());
    }

    @Test
    void findAllActiveReturnsOnlyActiveCustomers() {
        // Given
        when(customerRepository.findByActiveTrue()).thenReturn(List.of(new Customer()));

        // When
        List<Customer> resultado = service.findAllActive();

        // Then
        assertEquals(1, resultado.size());
    }
}
