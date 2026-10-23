package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    private MimeMessage criarMimeMessage() {
        return new MimeMessage(Session.getInstance(new Properties()));
    }

    @Test
    void deveSubstituirOCodigoNoTemplate() throws Exception {
        String html = service.loadTemplate("123456");

        assertTrue(html.contains("123456"));
        assertFalse(html.contains("{{CODE}}"));
    }

    @Test
    void deveEnviarEmail() throws Exception {
        MimeMessage message = criarMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(message);

        service.sendEmail("cliente@teste.com", "123456");

        verify(mailSender).send(message);
        assertEquals("Recuperação de Senha", message.getSubject());
    }

    @Test
    void deveLancarErroQuandoEnvioFalha() {
        when(mailSender.createMimeMessage()).thenReturn(criarMimeMessage());
        doThrow(new MailSendException("falha")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> service.sendEmail("cliente@teste.com", "123456"));
    }
}