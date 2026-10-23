package sgcf_back.camilaronzzani.com.github.sgcf_back.controllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.PaymentController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PaymentRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Payment;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.CurrencyService;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private CurrencyService currencyService;

    private ObjectMapper objectMapper;

    private Payment samplePayment;
    private PaymentRequest sampleRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        Customer sampleCustomer = new Customer();
        sampleCustomer.setId(1L);

        samplePayment = new Payment();
        samplePayment.setId(1L);
        samplePayment.setStatus(Status.PENDING);
        samplePayment.setCustomer(sampleCustomer);
        samplePayment.setTotalAccount(300.0);
        samplePayment.setActive(true);

        sampleRequest = new PaymentRequest(1L, Status.PENDING, 300.0);
    }

    @Test
    @DisplayName("Listar todos os pagamentos")
    void deveRetornarListaDePagamentos() throws Exception {
        // Given
        when(paymentService.findAll()).thenReturn(List.of(samplePayment));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/Payment/findAll"));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].totalAccount").value(300.0));
        verify(paymentService, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar pagamento pelo id")
    void deveRetornarPagamentoPeloId() throws Exception {
        // Given
        when(paymentService.findById(1L)).thenReturn(samplePayment);

        // When
        ResultActions resposta = mockMvc.perform(get("/api/Payment/findId/{id}", 1L));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customer.id").value(1L));
        verify(paymentService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Salvar pagamento")
    void deveSalvarPagamentoERetornar201() throws Exception {
        // Given
        doNothing().when(paymentService).save(any(PaymentRequest.class));

        // When
        ResultActions resposta = mockMvc.perform(post("/api/Payment/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        // Then
        resposta.andExpect(status().isCreated());
        verify(paymentService, times(1)).save(any(PaymentRequest.class));
    }

    @Test
    @DisplayName("Atualizar pagamento")
    void deveAtualizarPagamento() throws Exception {
        // Given
        when(paymentService.update(any(PaymentRequest.class), eq(1L))).thenReturn(samplePayment);

        // When
        ResultActions resposta = mockMvc.perform(put("/api/Payment/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(paymentService, times(1)).update(any(PaymentRequest.class), eq(1L));
    }

    @Test
    @DisplayName("Deletar pagamento")
    void deveDeletarPagamentoERetornar204() throws Exception {
        // Given
        doNothing().when(paymentService).delete(1L);

        // When
        ResultActions resposta = mockMvc.perform(delete("/api/Payment/delete/{id}", 1L));

        // Then
        resposta.andExpect(status().isNoContent());
        verify(paymentService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Listar pagamentos ativos")
    void deveRetornarListaDePagamentosAtivos() throws Exception {
        // Given
        when(paymentService.findAllActive()).thenReturn(List.of(samplePayment));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/Payment/findAll/active"));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(paymentService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Buscar pagamentos do cliente")
    void deveRetornarPagamentosDoCliente() throws Exception {
        // Given
        when(paymentService.findByCustomer(1L)).thenReturn(List.of(samplePayment));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/Payment/findByCustomer/{customerId}", 1L));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(paymentService, times(1)).findByCustomer(1L);
    }

    @Test
    @DisplayName("Buscar pagamentos pelo status")
    void deveRetornarPagamentosPeloStatus() throws Exception {
        // Given
        when(paymentService.findByStatus(Status.PENDING)).thenReturn(List.of(samplePayment));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/Payment/findByStatus/{status}", Status.PENDING));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(paymentService, times(1)).findByStatus(Status.PENDING);
    }

    @Test
    @DisplayName("Buscar cotação do dólar")
    void deveRetornarCotacaoDoDolar() throws Exception {
        // Given
        when(currencyService.getQuotation("USD")).thenReturn(5.25);

        // When
        ResultActions resposta = mockMvc.perform(get("/api/Payment/quotations"));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5.25));
        verify(currencyService, times(1)).getQuotation("USD");
    }
}
