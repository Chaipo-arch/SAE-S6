package sae.semestre.six.domain.doctor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDTO {
    private Long id;
    private String doctorNumber;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phoneNumber;
    private String email;
    private String department;

    // Constructeur qui convertit un Doctor en DoctorDTO
    public DoctorDTO(Doctor doctor) {
        this.id = doctor.getId();
        this.doctorNumber = doctor.getDoctorNumber();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.specialization = doctor.getSpecialization();
        this.phoneNumber = doctor.getPhoneNumber();
        this.email = doctor.getEmail();
        this.department = doctor.getDepartment();
    }
}