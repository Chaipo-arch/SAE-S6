package sae.semestre.six.domain.room;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import sae.semestre.six.domain.appointment.Appointment;
import sae.semestre.six.domain.appointment.AppointmentDao;
import sae.semestre.six.exception.ResourceNotFoundException;

@Service
public class RoomService {

    private final RoomDao roomDao;
    private final AppointmentDao appointmentDao;

    public RoomService(RoomDao roomDao, AppointmentDao appointmentDao) {
        this.roomDao = roomDao;
        this.appointmentDao = appointmentDao;
    }

    public void assignAppointmentToRoom(Long appointmentId, String roomNumber) {
        Room room = roomDao.findByRoomNumber(roomNumber);
        if(room == null) {
            throw new ResourceNotFoundException("The room was not found");
        }

        Appointment appointment = appointmentDao.findById(appointmentId);

        if(appointment == null) {
            throw new ResourceNotFoundException("The appointment was not found");
        }

        room.assignAppointment(appointment);

        roomDao.update(room);
        appointmentDao.update(appointment);
    }

    public RoomAvailabilityInformation getRoomAvailability(String roomNumber) {
        Room room = roomDao.findByRoomNumber(roomNumber);

        if(room == null) {
            throw new ResourceNotFoundException("The room was not found");
        }

        return room.getAvailability();

    }
}
