package sae.semestre.six.mail;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

@SpringBootTest
public class EmailServiceTest {

    @Mock
    private MailSender mockMailSender;

    @Test
    public void sendEmailTest() {
        String to = "Mail@mgmail.com";
        String subject = "exemple";
        String body = "no";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EmailService.EMAIL_SOURCE.HOSPITAL.getEmail());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        EmailService emailService = new GmailService(mockMailSender);
        emailService.sendEmail(to,subject,body);
        Mockito.verify(mockMailSender).send(message);
    }
}
