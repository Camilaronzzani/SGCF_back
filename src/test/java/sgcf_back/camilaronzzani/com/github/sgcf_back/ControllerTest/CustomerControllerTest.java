package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.CustomerController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.CustomerRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.CountryCustomer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper;

    private Customer sampleCustomer;
    private CustomerRequest sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();

        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setCpf("52998224725");
        sampleCustomer.setName("Camila");
        sampleCustomer.setLanguageSpeak(List.of(Language.Portuguese));
        sampleCustomer.setCountryCustomer(CountryCustomer.Brazil);
        sampleCustomer.setEmail("camila@email.com");
        sampleCustomer.setActive(true);

        sampleRequest = new CustomerRequest(
                null,
                "52998224725",
                "Camila",
                List.of(Language.Portuguese),
                CountryCustomer.Brazil,
                "camila@email.com"
        );
    }

    @Test
    @DisplayName("Listar todos os clientes")
    void findAll_ShouldReturnList() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(sampleCustomer));

        mockMvc.perform(get("/api/customer/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Camila"));

        verify(customerService, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar cliente pelo id")
    void findById_ShouldReturnCustomer() throws Exception {
        when(customerService.findById(1L)).thenReturn(sampleCustomer);

        mockMvc.perform(get("/api/customer/findById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("camila@email.com"));

        verify(customerService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Salvar cliente")
    void save_ShouldReturnCreated() throws Exception {
        doNothing().when(customerService).save(any(CustomerRequest.class));

        mockMvc.perform(post("/api/customer/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated());

        verify(customerService, times(1)).save(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Atualizar cliente")
    void update_ShouldReturnUpdatedCustomer() throws Exception {
        when(customerService.update(any(CustomerRequest.class), eq(1L))).thenReturn(sampleCustomer);

        mockMvc.perform(put("/api/customer/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(customerService, times(1)).update(any(CustomerRequest.class), eq(1L));
    }

    @Test
    @DisplayName("Deletar cliente")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(customerService).delete(1L);

        mockMvc.perform(delete("/api/customer/delete/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Listar clientes ativos")
    void findAllActive_ShouldReturnList() throws Exception {
        when(customerService.findAllActive()).thenReturn(List.of(sampleCustomer));

        mockMvc.perform(get("/api/customer/findAll/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(customerService, times(1)).findAllActive();
    }
}
