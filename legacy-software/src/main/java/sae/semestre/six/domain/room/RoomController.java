package sae.semestre.six.domain.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.appointment.AppointmentDao;
import sae.semestre.six.domain.appointment.Appointment;

import java.util.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    
    @Autowired
    private RoomDao roomDao;
    
    @Autowired
    private AppointmentDao appointmentDao;
    
    
    @PostMapping("/assign")
    public String assignRoom(@RequestParam Long appointmentId, @RequestParam String roomNumber) {
        try {
            Room room = roomDao.findByRoomNumber(roomNumber);
            Appointment appointment = appointmentDao.findById(appointmentId);

            room.assignAppointment(appointment);
            
            roomDao.update(room);
            appointmentDao.update(appointment);
            
            return "Room assigned successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    
    @GetMapping("/availability")
    public RoomAvailabilityInformation getRoomAvailability(@RequestParam String roomNumber) {
        Room room = roomDao.findByRoomNumber(roomNumber);

        return room.getAvailability();
    }
} 