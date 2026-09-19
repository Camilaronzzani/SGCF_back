package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.AwesomeApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@Service
public class CurrencyService {

    private final RestClient restClient;

    public CurrencyService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://economia.awesomeapi.com.br")
                .build();
    }

    public double getQuotation(String currency) {

        Map<String, AwesomeApiResponse> response = restClient
                .get()
                .uri("/json/last/{currency}-BRL", currency)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        AwesomeApiResponse quotation = response.values()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cotação não encontrada"
                ));
        log.info("today the quotations are {}" , quotation.getBid());

        return Double.parseDouble(quotation.getBid());
    }
}
