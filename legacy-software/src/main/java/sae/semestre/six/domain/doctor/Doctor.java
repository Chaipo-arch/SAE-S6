package sae.semestre.six.domain.doctor;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import sae.semestre.six.domain.appointment.Appointment;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro du docteur est obligatoire")
    @Column(name = "doctor_number", unique = true, nullable = false)
    private String doctorNumber;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "department")
    private String department;

    @Builder.Default
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private Set<Appointment> appointments;

    public void addAppointment(Appointment appointment) {
        if(appointments ==null) {
            appointments = new HashSet<>();
        }
        if(appointments.contains(appointment)) return;
        this.appointments.add(appointment);
        appointment.addDoctor(this);
    }

    public boolean isSpecialization(String specialization) {
        return this.specialization.equals(specialization);
    }

    
    public Doctor() {
    }


}