package sae.semestre.six.domain.room;

import sae.semestre.six.domain.appointment.Appointment;

import jakarta.persistence.*;
import sae.semestre.six.exception.InvalidDataException;

import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
    
    @OneToMany(mappedBy = "room")
    private Set<Appointment> appointments = new HashSet<>();
    
    
    @Column(name = "current_patient_count")
    private Integer currentPatientCount = 0;
    
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getCapacity() {
        return capacity;
    }

    public RoomAvailabilityInformation getAvailability() {
        return  new RoomAvailabilityInformation(getRoomNumber(),
                getCapacity(),getCurrentPatientCount(),
                canAcceptPatient());
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }
    
    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    public Integer getCurrentPatientCount() {
        return currentPatientCount;
    }
    
    public void setCurrentPatientCount(Integer currentPatientCount) {
        this.currentPatientCount = currentPatientCount;
        
        this.isOccupied = currentPatientCount >= capacity;
    }

    public void assignAppointment(Appointment appointment) {
        if(appointments.contains(appointment)) return;
        if (canSurgery(appointment)) {
            throw new InvalidDataException("Error: Only surgeons can use surgery rooms");
        }

        if (currentPatientCount >= capacity) {
            throw new InvalidDataException("Error: Room is at full capacity");
        }

        setCurrentPatientCount(currentPatientCount + 1);
        appointments.add(appointment);
        appointment.assignRoom(this);
    }

    public boolean canSurgery(Appointment appointment) {
        return getType().equals("SURGERY") && !appointment.getDoctor().isSpecialization("SURGEON");

    }
    
    
    public boolean canAcceptPatient() {
        return currentPatientCount < capacity && !isOccupied;
    }
}