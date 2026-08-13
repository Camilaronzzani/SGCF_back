package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String to, String code) throws Exception {

        String html = loadTemplate(code);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage , true , "UTF-8");

        helper.setTo(to);
        helper.setSubject("Recuperação de Senha");
        helper.setText(html , true);

        mailSender.send(mimeMessage);
    }
    public String loadTemplate(String code) throws Exception {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("Email.html");
        if (input == null) {
            throw new IllegalStateException("Template Email.html não encontrado no classpath");
        }

        String html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        ;

        return html.replace("{{CODE}}", code);
    }
}
