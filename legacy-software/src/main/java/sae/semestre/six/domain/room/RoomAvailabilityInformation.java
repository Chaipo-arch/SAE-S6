package sae.semestre.six.domain.room;

public record RoomAvailabilityInformation(String roomNumber, int capacity, int patientCount, boolean canAcceptPatient) {
}
