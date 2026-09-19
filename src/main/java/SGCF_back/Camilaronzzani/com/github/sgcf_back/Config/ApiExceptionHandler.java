package SGCF_back.Camilaronzzani.com.github.sgcf_back.Config;

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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException exception) {
        log.error("Request failed: {}", exception.getReason());

        Map<String, String> erro = new HashMap<>();
        erro.put("message", exception.getReason());

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

        if (causa instanceof ResponseStatusException) {
            return handleResponseStatus((ResponseStatusException) causa);
        }

        if (exception instanceof ErrorResponse errorResponse) {
            Map<String, String> aviso = new HashMap<>();
            aviso.put("message", exception.getMessage());

            return ResponseEntity.status(errorResponse.getStatusCode()).body(aviso);
        }

        log.error("Unexpected error", exception);

        Map<String, String> erro = new HashMap<>();
        erro.put("message", "erro ao processar a requisicao");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
