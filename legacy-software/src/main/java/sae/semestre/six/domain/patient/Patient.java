package sae.semestre.six.domain.patient;

import sae.semestre.six.domain.appointment.Appointment;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
public class Patient {

    private static final String PHONE_NUMBER_PATTERN = "??";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_number", unique = true, nullable = false)
    private String patientNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments = new HashSet<>();

    
    public Patient() {
    }

    public void setGender(String gender) {
        if(!isExistingGender(gender)) {
            throw new RuntimeException("Veuillez choisir un sexe entre H ou F.");
        }
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(!isCorrectPhoneNumber(phoneNumber)) {
            throw new RuntimeException("Le numéro de téléphone n'est pas correcte.");
        }
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    private boolean isExistingGender(String gender) {
        return gender.equals("F") || gender.equals("H") ;

    }

    private boolean isCorrectPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(phoneNumber);
    }
} 