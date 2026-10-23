package sgcf_back.camilaronzzani.com.github.sgcf_back.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j //log
public class ApiExceptionHandler {

    private static final String MESSAGE = "message";

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException exception) {
        log.error("Request failed: {}", exception.getReason());

        Map<String, String> erro = new HashMap<>();
        erro.put(MESSAGE, exception.getReason());

        return ResponseEntity.status(exception.getStatusCode()).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> erros = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(campo ->
                erros.put(campo.getField(), campo.getDefaultMessage()));

        log.error("Validation failed: {}", erros);
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
        Throwable causa = exception;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }

        if (causa instanceof ResponseStatusException responseStatusException) {
            return handleResponseStatus(responseStatusException);
        }

        if (exception instanceof ErrorResponse errorResponse) {
            Map<String, String> aviso = new HashMap<>();
            aviso.put(MESSAGE, exception.getMessage());

            return ResponseEntity.status(errorResponse.getStatusCode()).body(aviso);
        }

        log.error("Unexpected error", exception);

        Map<String, String> erro = new HashMap<>();
        erro.put(MESSAGE, "erro ao processar a requisicao");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
