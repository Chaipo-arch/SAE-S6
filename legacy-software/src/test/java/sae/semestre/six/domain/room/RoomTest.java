package sae.semestre.six.domain.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sae.semestre.six.domain.appointment.Appointment;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.exception.InvalidDataException;

import static org.junit.jupiter.api.Assertions.*;


class RoomTest {

    private Appointment appointment;
    private Room room;
    @BeforeEach
    void setUp() {
        appointment = Appointment.builder().appointmentNumber("App01").status("SCHEDULED").build();
        room = Room.builder().type("JesaisPas").roomNumber("Room10").capacity(10)
                .currentPatientCount(0).build();
    }

    @Test
    void assignAppointment_success() {
        room.assignAppointment(appointment);
        assertEquals(1,room.getCurrentPatientCount());
        assertEquals(room.getRoomNumber(),appointment.getRoomNumber());
    }

    @Test
    void assignAppointment_tooMuchAppointment() {
        Room room2 = Room.builder().roomNumber("App02").type("JeSaisPas")
                .capacity(3).currentPatientCount(3).build();
        assertThrows(InvalidDataException.class,()->room2.assignAppointment(appointment));
    }

    @Test
    void assignAppointment_sameAppointment() {
        room.assignAppointment(appointment);
        room.assignAppointment(appointment);
        assertEquals(1,room.getCurrentPatientCount());
    }

    @Test
    void assignAppointment_SurgeonForSurgeryRoom() {
        Doctor surgeon = Doctor.builder().specialization("SURGEON").build();
        Appointment appointmentSurgeon = Appointment.builder().appointmentNumber("App01")
                .status("SCHEDULED").doctor(surgeon).build();
        Room roomSurgery = Room.builder().roomNumber("App02").type("SURGERY")
                .capacity(3).currentPatientCount(0).build();
        roomSurgery.assignAppointment(appointmentSurgeon);
        assertEquals(1,roomSurgery.getCurrentPatientCount());
        assertEquals("App02",appointmentSurgeon.getRoomNumber());
    }

    @Test
    void assignAppointment_NotSurgeonForSurgeryRoom() {
        Doctor notSurgeon = Doctor.builder().specialization("SURG").build();
        Appointment appointmentSurgeon = Appointment.builder().appointmentNumber("App01")
                .status("SCHEDULED").doctor(notSurgeon).build();
        Room roomSurgery = Room.builder().roomNumber("App02").type("SURGERY")
                .capacity(3).currentPatientCount(0).build();
        assertThrows(InvalidDataException.class,()->roomSurgery.assignAppointment(appointmentSurgeon));
    }

}