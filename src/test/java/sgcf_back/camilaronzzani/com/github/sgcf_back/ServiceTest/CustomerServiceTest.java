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
    void deveListarTodosOsClientes() {
        Customer c = new Customer();
        when(customerRepository.findAll()).thenReturn(List.of(c));

        List<Customer> resultado = service.findAll();
        assertEquals(1, resultado.size());
    }

    @Test
    void deveBuscarClientePorId() {
        Customer c = new Customer();
        c.setName("Maria");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        Customer resultado = service.findById(1L);
        assertEquals("Maria", resultado.getName());
    }

    @Test
    void deveLancarErroQuandoClienteNaoExiste() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(99L));
        assertTrue(ex.getCause() instanceof ResponseStatusException);
    }

    @Test
    void deveSalvarCliente() {
        CustomerRequest request = mock(CustomerRequest.class);

        service.save(request);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void deveAtualizarDadosDoCliente() {
        Customer c = new Customer();
        c.setName("Antigo");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        CustomerRequest request = mock(CustomerRequest.class);
        when(request.name()).thenReturn("Novo");

        Customer resultado = service.update(request, 1L);
        assertEquals("Novo", resultado.getName());
    }

    @Test
    void deveDesativarCliente() {
        Customer c = new Customer();
        c.setActive(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        service.delete(1L);
        assertFalse(c.isActive());
    }

    @Test
    void deveListarApenasClientesAtivos() {
        when(customerRepository.findByActiveTrue()).thenReturn(List.of(new Customer()));

        List<Customer> resultado = service.findAllActive();

        assertEquals(1, resultado.size());
    }
}