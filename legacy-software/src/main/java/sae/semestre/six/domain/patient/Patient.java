package sae.semestre.six.domain.patient;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sae.semestre.six.domain.appointment.Appointment;

import jakarta.persistence.*;
import sae.semestre.six.exception.InvalidDataException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
@AllArgsConstructor
@Builder(builderClassName = "PatientBuilder")
@Getter
/**
 * Une entité patient.
 *
 */
public class Patient {

    private static final String PHONE_NUMBER_PATTERN = "^[0-9]\\d{9}$";

    private static final String EMAIL_PATTERN = "^[a-zA-Z]{1,}@[a-zA-Z]{1,}\\.[a-zA-Z]{1,3}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_number", unique = true, nullable = false)
    private String patientNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "gender")
    @Pattern(regexp = "^[HF]$",message = "Le genre doit être H ou F.")
    private String gender;

    @Column(name = "address")
    private String address;

    @Pattern(regexp = PHONE_NUMBER_PATTERN, message = "Le numéro de téléphone n'est pas correcte.")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Builder.Default
    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments = new HashSet<>();


    public Patient() {
    }

    public Long getId() {
        return id;
    }

    private static boolean isExistingGender(String gender) {
        return gender.equals("F") || gender.equals("H") ;

    }

    private static boolean isCorrectPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_NUMBER_PATTERN);
    }
    public void checkCorrectness() {
        clone(getId());
    }

    public Patient clone(Long id) {
        return Patient.builder()
                .patientNumber(patientNumber)
                .gender(gender).phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth).lastName(lastName)
                .firstName(firstName).address(address)
                .appointments(appointments).id(id).build();
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setPatient(this);
    }

    /**
     * https://www.baeldung.com/lombok-builder-custom-setter
     * Builder personnalisé pour permettre d'ajouter des contraintes de création plus facilement.
     */
    public static class PatientBuilder {

        private String phoneNumber;

        private String gender;
        private  String patientNumber;
        private String firstName;
        private String lastName;
        private String email;

        public PatientBuilder phoneNumber(String phoneNumber) {
            if(phoneNumber != null) {

                if (!isCorrectPhoneNumber(phoneNumber)) {
                    throw new InvalidDataException("The phone number is incorrect");
                }
                this.phoneNumber = phoneNumber;
            }
            return this;
        }

        public PatientBuilder gender(String gender) {
            if(gender != null) {
                if (!isExistingGender(gender)) {
                    throw new InvalidDataException("The gender has to be 'H' or 'F'");
                }
                this.gender = gender;
            }

            return this;
        }

        public PatientBuilder patientNumber(String patientNumber) {
            if(patientNumber == null) {
                throw new InvalidDataException("The patient should have a patient number");
            }
            this.patientNumber = patientNumber;
            return this;
        }

        public PatientBuilder firstName(String firstName) {
            if(firstName == null) {
                throw new InvalidDataException("The patient should have a first name");
            }
            this.firstName = firstName;
            return this;
        }

        public PatientBuilder lastName(String lastName) {
            if(lastName == null) {
                throw new InvalidDataException("The patient should have a last name");
            }
            this.lastName = lastName;
            return this;
        }

        public PatientBuilder email(String email) {
            if(email == null) {
                throw new InvalidDataException("The patient should have an email");
            }
            if(!email.matches(EMAIL_PATTERN)) {
                throw new InvalidDataException("The email must follow an email convention");
            }
            this.email = email;
            return this;

        }


    }

}