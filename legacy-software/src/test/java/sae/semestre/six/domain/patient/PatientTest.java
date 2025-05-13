package sae.semestre.six.domain.patient;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {

    @Test
    public void defineWithExistingGenderTest() {
        Patient patient = new Patient();
        assertDoesNotThrow(() -> patient.setGender("H"),"Le sexe Homme doit pouvoir être assigné.");
        assertDoesNotThrow(() -> patient.setGender("F"),"Le sexe Femme doit pouvoir être assigné.");
    }

    @Test
    public void defineWithNonExistingGenderTest() {
        Patient patient = new Patient();
        assertThrows(RuntimeException.class,()->patient.setGender("Helicoptére"));
    }
}
