package sgcf_back.camilaronzzani.com.github.sgcf_back.serviceTest;

import org.springframework.http.HttpStatus;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.CountryCustomer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
import sgcf_back.camilaronzzani.com.github.sgcf_back.exception.ServiceException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.CustomerService;
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

import static org.junit.jupiter.api.Assertions.*;
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
        Customer c = new Customer();
        when(customerRepository.findAll()).thenReturn(List.of(c));

        List<Customer> resultado = service.findAll();

        assertEquals(1, resultado.size());
    }

    @Test
    void findByIdReturnsCustomerWhenItExists() {
        Customer c = new Customer();
        c.setName("Maria");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        Customer resultado = service.findById(1L);

        assertEquals("Maria", resultado.getName());
    }

    @Test
    void findByIdThrowsWhenCustomerDoesNotExist() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(99L));

        assertInstanceOf(ResponseStatusException.class, ex.getCause());
    }

    @Test
    void saveStoresCustomerInRepository() {
        CustomerRequest request = mock(CustomerRequest.class);

        service.save(request);

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateChangesCustomerData() {
        Customer c = new Customer();
        c.setName("Antigo");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        CustomerRequest request = mock(CustomerRequest.class);
        when(request.name()).thenReturn("Novo");

        Customer resultado = service.update(request, 1L);

        assertEquals("Novo", resultado.getName());
    }

    @Test
    void deleteDeactivatesCustomer() {
        Customer c = new Customer();
        c.setActive(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        service.delete(1L);

        assertFalse(c.isActive());
    }

    @Test
    void findAllActiveReturnsOnlyActiveCustomers() {
        when(customerRepository.findByActiveTrue()).thenReturn(List.of(new Customer()));

        List<Customer> resultado = service.findAllActive();

        assertEquals(1, resultado.size());
    }

    @Test
    void findAllThrowsServiceExceptionWhenRepositoryFails() {
        when(customerRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.findAll());
    }

    @Test
    void findByIdThrowsServiceExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> service.findById(1L));
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void findByIdThrowsServiceExceptionWhenRepositoryFails() {
        when(customerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.findById(1L));
    }

    @Test
    void saveThrowsServiceExceptionWhenRepositoryFails() {
        CustomerRequest request = new CustomerRequest(
                "00000000000191",
                "00000000000",
                "Maria",
                List.of(Language.PORTUGUESE),
                CountryCustomer.BRAZIL,
                "maria@email.com"
        );        when(customerRepository.save(any())).thenThrow(new RuntimeException("Save error"));

        assertThrows(ServiceException.class, () -> service.save(request));
    }

    @Test
    void updateThrowsServiceExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        CustomerRequest request = new CustomerRequest(
                "00000000000191",
                "00000000000",
                "Maria",
                List.of(Language.PORTUGUESE),
                CountryCustomer.BRAZIL,
                "maria@email.com"
        );
        ServiceException exception = assertThrows(ServiceException.class, () -> service.update(request, 1L));
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void updateThrowsServiceExceptionWhenRepositoryFails() {
        when(customerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));
        CustomerRequest request = new CustomerRequest(
                "00000000000191",
                "00000000000",
                "Maria",
                List.of(Language.PORTUGUESE),
                CountryCustomer.BRAZIL,
                "maria@email.com"
        );

        assertThrows(ServiceException.class, () -> service.update(request, 1L));
    }

    @Test
    void deleteThrowsServiceExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> service.delete(1L));
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void deleteThrowsServiceExceptionWhenRepositoryFails() {
        when(customerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.delete(1L));
    }

    @Test
    void findAllActiveThrowsServiceExceptionWhenRepositoryFails() {
        when(customerRepository.findByActiveTrue()).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.findAllActive());
    }
}
