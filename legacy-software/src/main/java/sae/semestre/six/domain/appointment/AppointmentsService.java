package sae.semestre.six.domain.appointment;

import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.room.Room;
import sae.semestre.six.domain.room.RoomDao;

import java.time.LocalDateTime;

public class AppointmentsService {

    private AppointmentDao appointmentDao;
    private RoomDao roomDao;
    private DoctorDao doctorDao;

    public AppointmentsService(AppointmentDao appointmentDao , RoomDao roomDao, DoctorDao doctorDao) {
        this.appointmentDao = appointmentDao;
        this.roomDao = roomDao;
        this.doctorDao = doctorDao;
    }
    public void assignRoom(Long appointmentId, String roomNumber) {
        roomDao.findByRoomNumber(roomNumber).assignAppointment(appointmentDao.findById(appointmentId));

    }
    public void assignPatient(long patientId){

    }
    public void assignDoctor(long doctorId){

    }
    public static Appointment create(Doctor doctor, Patient patient, Room room, LocalDateTime dateTime) {

        Appointment appt = new Appointment();
        appt.setDoctor(doctor);
        appt.setPatient(patient);
        appt.setRoomNumber(room.getRoomNumber());
        appt.setAppointmentDate(dateTime);
        appt.setStatus("SCHEDULED");
        appt.setAppointmentNumber("APPT" + System.currentTimeMillis());
        appt.save();


        return appt;
    }
}
