package sae.semestre.six.domain.room;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    
    
    @PostMapping("/assign")
    public ResponseEntity<String> assignRoom(@RequestParam Long appointmentId, @RequestParam String roomNumber) {
            roomService.assignAppointmentToRoom(appointmentId,roomNumber);
            return ResponseEntity.ok("Room assigned successfully");

    }
    
    @GetMapping("/availability")
    public ResponseEntity<RoomAvailabilityInformation> getRoomAvailability(@RequestParam String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomAvailability(roomNumber));
    }
} 