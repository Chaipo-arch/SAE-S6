package sae.semestre.six.domain.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import sae.semestre.six.exception.InvalidDataException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {


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

    @BeforeEach
    void setUp() {
        patientInformationCorrect = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").lastName("C").gender("H").build();
        patientInformationIncorrectFirstName = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").lastName("C").gender("H").build();
        patientInformationIncorrectLastName = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").gender("H").build();
        patientInformationIncorrectPhoneNumber = PatientInformation.builder().patientNumber("123").phoneNumber("1234567").firstName("E").gender("H").lastName("C").build();
        patientInformationIncorrectGender = PatientInformation.builder().patientNumber("123").phoneNumber("1234567891").firstName("E").lastName("C").gender("Abc").build();
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
                      .content(objectMapper.writeValueAsString(patientInformationCorrect)))
              .andExpect(status().isOk());

        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientInformationCorrect)))
                .andExpect(status().isBadRequest());
    }
}
