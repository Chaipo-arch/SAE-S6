package sae.semestre.six.mail;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    public enum EMAIL_SOURCE {
        SUPPLIER,
        ADMIN,
        HOSPITAL;
        public String getEmail() {
            return switch (this) {
                case ADMIN -> "admin@hospital.com";
                case SUPPLIER -> "supplier@example.com";
                case HOSPITAL -> "hospital.system@gmail.com";
            };
        }
    }

    public void sendEmail(String to, String subject, String body) ;
}
