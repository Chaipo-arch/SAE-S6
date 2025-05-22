package sae.semestre.six.domain.room;

import lombok.Builder;
import lombok.Getter;
import sae.semestre.six.domain.appointment.Appointment;

import jakarta.persistence.*;
import sae.semestre.six.exception.InvalidDataException;

import java.util.Set;
import java.util.HashSet;

@Builder(builderClassName = "RoomBuilder")
@Entity
@Table(name = "rooms")
public class Room {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(name = "room_number", unique = true)
    private String roomNumber;
    
    @Column(name = "floor")
    private Integer floor;
    
    @Column(name = "type")
    private String type; 
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "is_occupied")
    private Boolean isOccupied = false;

    @Builder.Default
    @OneToMany(mappedBy = "room")
    private Set<Appointment> appointments = new HashSet<>();

    @Getter
    @Column(name = "current_patient_count")
    private Integer currentPatientCount = 0;

    public RoomAvailabilityInformation getAvailability() {
        return  new RoomAvailabilityInformation(roomNumber,
                capacity,currentPatientCount,
                canAcceptPatient());
    }
    
    public void setCurrentPatientCount(Integer currentPatientCount) {
        this.currentPatientCount = currentPatientCount;
        
        this.isOccupied = currentPatientCount >= capacity;
    }

    public void assignAppointment(Appointment appointment) {
        if(appointments.contains(appointment)) return;

        if (isOfType("SURGERY") && !appointment.getDoctor().isSpecialization("SURGEON")) {
            throw new InvalidDataException("Error: Only surgeons can use surgery rooms");
        }

        if (currentPatientCount >= capacity) {
            throw new InvalidDataException("Error: Room is at full capacity");
        }

        setCurrentPatientCount(currentPatientCount + 1);
        appointments.add(appointment);
        appointment.assignRoom(this);
    }

    private boolean isOfType(String type) {
        return this.type.equals(type);
    }
    
    public boolean canAcceptPatient() {
        return currentPatientCount < capacity && !isOccupied;
    }

    public static class RoomBuilder {

    }
}