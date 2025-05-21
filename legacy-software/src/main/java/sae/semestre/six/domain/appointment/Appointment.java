package sae.semestre.six.domain.appointment;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.history.PatientHistory;
import sae.semestre.six.domain.room.Room;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "appointments")
@Builder
@AllArgsConstructor
public class Appointment {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "appointment_number", unique = true, nullable = false)
    private String appointmentNumber;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    private Room room;

    @ManyToOne
    private PatientHistory patientHistory;

    @Setter
    @Getter
    @Column(name = "appointment_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime appointmentDate;


    @Setter
    @Column(name = "status")
    private String status;

    @Getter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "room_number")
    private String roomNumber;



    public Appointment() {
    }

    public void assignRoom(Room room) {
        this.room = room;
        room.assignAppointment(this);
    }

    public void addPatient(Patient patient) {
        if(this.patient == patient) return;
        this.patient = patient;
        patient.addAppointment(this);
    }

    public void addDoctor(Doctor doctor) {
        if(this.doctor == doctor) return;
        this.doctor = doctor;
        doctor.addAppointment(this);
    }


}