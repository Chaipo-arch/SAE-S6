package sae.semestre.six.domain.room;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST permettant de gérer les opérations liées aux salles (rooms),
 * comme l'assignation de rendez-vous ou la consultation de la disponibilité.
 */
@RestController
@RequestMapping("/rooms")
public class RoomController {
    
    private final RoomService roomService;

    /**
     * Constructeur du contrôleur des salles.
     * @param roomService Service métier associé à la gestion des salles.
     */
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Assigne un rendez-vous à une salle spécifique.
     *
     * @param appointmentId L'identifiant du rendez-vous à assigner.
     * @param roomNumber Le numéro de la salle dans laquelle assigner le rendez-vous.
     * @return Une réponse HTTP indiquant le succès de l'opération.
     */
    @PostMapping("/assign")
    public ResponseEntity<String> assignAppointment(@RequestParam Long appointmentId, @RequestParam String roomNumber) {
            roomService.assignAppointmentToRoom(appointmentId,roomNumber);
            return ResponseEntity.ok("Room assigned successfully");

    }

    /**
     * Récupère les informations de disponibilité d'une salle à partir de son numéro.
     *
     * @param roomNumber Le numéro de la salle dont on souhaite connaître la disponibilité.
     * @return Une réponse HTTP contenant les informations de disponibilité de la salle.
     */
    @GetMapping("/availability")
    public ResponseEntity<RoomAvailabilityInformation> getRoomAvailability(@RequestParam String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomAvailability(roomNumber));
    }
} 