package sae.semestre.six.domain.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientInformation {

    private String patientNumber;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String phoneNumber;

    private String email;

    public Patient toPatient(Long id) {
        return Patient.builder()
                .patientNumber(patientNumber)
                .gender(gender).phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth).email(email).lastName(lastName)
                .firstName(firstName).address(address).id(id).build();
    }
}
