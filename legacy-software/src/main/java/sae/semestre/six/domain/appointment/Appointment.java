package sae.semestre.six.domain.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.history.PatientHistory;
import sae.semestre.six.domain.room.Room;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "appointments")
@Builder
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", unique = true, nullable = false)
    private String appointmentNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    private Room room;

    @ManyToOne
    private PatientHistory patientHistory;

    @Column(name = "appointment_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentDate;


    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "room_number")
    private String roomNumber;

    
    public Appointment() {
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
} 