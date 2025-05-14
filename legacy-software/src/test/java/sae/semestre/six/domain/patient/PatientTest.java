package sae.semestre.six.domain.patient;

import org.junit.jupiter.api.Test;
import sae.semestre.six.exception.InvalidDataException;

import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {

    @Test
    public void defineWithExistingGenderTest() {
        assertDoesNotThrow(() -> Patient.builder().gender("H").build(),"Le sexe Homme doit pouvoir être assigné avec le builder..");
        assertDoesNotThrow(() -> Patient.builder().gender("F").build(),"Le sexe Femme doit pouvoir être assigné avec le builder..");
    }

    @Test
    public void defineWithNonExistingGenderTest() {
        assertThrows(InvalidDataException.class,()->Patient.builder().gender("Heli").build());
    }
}
