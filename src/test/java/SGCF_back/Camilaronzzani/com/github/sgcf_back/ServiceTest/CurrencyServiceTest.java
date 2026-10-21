package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CurrencyServiceTest {

    private MockRestServiceServer server;
    private CurrencyService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        service = new CurrencyService(builder);
    }

    @Test
    void deveRetornarCotacao() {
        String json = "{\"USDBRL\": {\"bid\": \"5.35\"}}";

        server.expect(requestTo("https://economia.awesomeapi.com.br/json/last/USD-BRL"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        double resultado = service.getQuotation("USD");
        assertEquals(5.35, resultado, 0.001);
    }

    @Test
    void deveLancarErroQuandoRespostaVazia() {
        server.expect(requestTo("https://economia.awesomeapi.com.br/json/last/USD-BRL"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        assertThrows(ResponseStatusException.class, () -> service.getQuotation("USD"));
    }
}