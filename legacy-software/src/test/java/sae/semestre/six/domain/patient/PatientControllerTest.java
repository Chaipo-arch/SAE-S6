package sae.semestre.six.domain.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    public final Patient PATIENT = Patient.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").lastName("C").gender("H").build();

    @Autowired
    MockMvc mockMvc ;
    @Autowired
    private PatientController patientController;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientInformation patientInformationCorrect;
    private PatientInformation patientInformationIncorrectFirstName;
    private PatientInformation patientInformationIncorrectLastName;
    private PatientInformation patientInformationIncorrectPhoneNumber;
    private PatientInformation patientInformationIncorrectGender;
    private PatientInformation patientInformationIncorrectEmail;

    @BeforeEach
    void setUp() {
        patientInformationCorrect = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").lastName("C").email("e@e.e").gender("H").build();
        patientInformationIncorrectFirstName = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").lastName("C").gender("H").email("e@e.e").build();
        patientInformationIncorrectLastName = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").gender("H").email("e@e.e").build();
        patientInformationIncorrectPhoneNumber = PatientInformation.builder().patientNumber("123").phoneNumber("1234567").firstName("E").gender("H").email("e@e.e").lastName("C").build();
        patientInformationIncorrectGender = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").lastName("C").email("e@e.e").gender("Abc").build();
        patientInformationIncorrectEmail = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").lastName("C").email("e.e").gender("Abc").build();

    }

    @Test
    public void testCreationPatient() throws Exception {
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientInformationIncorrectFirstName)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientInformationIncorrectLastName)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientInformationIncorrectPhoneNumber)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientInformationIncorrectGender)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientInformationIncorrectEmail)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                      .content(objectMapper.writeValueAsString(patientInformationCorrect)))
              .andExpect(status().isCreated());

        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientInformationCorrect)))
                .andExpect(status().isBadRequest());
    }
}
