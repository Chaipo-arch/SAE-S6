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
import java.time.LocalTime;


@Entity
@Table(name = "appointments")
@Builder
@NoArgsConstructor
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

    @Getter
    @Column(name = "room_number")
    private String roomNumber;

    public void assignRoom(Room room) {
        if(room == null || this.room == room) return;
        this.room = room;
        this.roomNumber = room.getRoomNumber();
        room.assignAppointment(this);
    }

    public void addPatient(Patient patient) {
        if(patient ==  null || this.patient == patient) return;
        this.patient = patient;
        patient.addAppointment(this);
    }

    public void addDoctor(Doctor doctor) {
        if(doctor == null || this.doctor == doctor) return;
        this.doctor = doctor;
        doctor.addAppointment(this);
    }

    public boolean isSameDate(LocalDateTime localDateTime) {
        return appointmentDate.toLocalDate().equals(localDateTime.toLocalDate()) &&
                appointmentDate.getHour() == localDateTime.getHour();
    }

    public static boolean isWorkingHours(LocalTime time) {
        return time.isBefore(LocalTime.of(9, 0)) || time.isAfter(LocalTime.of(17, 0));
    }


}
