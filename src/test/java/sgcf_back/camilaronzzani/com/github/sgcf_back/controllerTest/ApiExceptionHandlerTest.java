package sgcf_back.camilaronzzani.com.github.sgcf_back.controllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.config.SecurityConfig;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.CustomerController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.exception.ServiceException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    @DisplayName("Erro inesperado retorna 500")
    void unexpectedErrorReturns500() throws Exception {
        when(customerService.findAll()).thenThrow(new IllegalStateException("database is down"));

        ResultActions resposta = mockMvc.perform(get("/api/customer/findAll"));

        resposta.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("erro ao processar a requisicao"));
    }

    @Test
    @DisplayName("Erro com causa 404 retorna 404")
    void wrappedNotFoundErrorReturns404() throws Exception {
        ResponseStatusException notFound = new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found");
        when(customerService.findAll()).thenThrow(new ServiceException(notFound));

        ResultActions resposta = mockMvc.perform(get("/api/customer/findAll"));

        resposta.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("customer not found"));
    }

    @Test
    @DisplayName("Método HTTP errado retorna 405")
    void wrongHttpMethodReturns405() throws Exception {
        ResultActions resposta = mockMvc.perform(post("/api/customer/findAll"));

        resposta.andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").exists());
    }
}
