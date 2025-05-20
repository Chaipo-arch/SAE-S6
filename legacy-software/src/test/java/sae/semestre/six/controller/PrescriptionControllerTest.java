package sae.semestre.six.controller;

import org.junit.Before;
import org.junit.Test;
import sae.semestre.six.domain.prescription.Prescription;
import sae.semestre.six.domain.prescription.PrescriptionController;
import sae.semestre.six.file.FileHandler;

import static org.junit.Assert.*;
import java.util.List;

public class PrescriptionControllerTest {
    
    private PrescriptionController prescriptionController;
    
    @Before
    public void setUp() {
        prescriptionController = new PrescriptionController(new FileHandler());
    }
    
    
    @Test
    public void testAddAndRetrievePrescription() {
        String result = prescriptionController.addPrescription(
            "PAT001",
            new String[]{"PARACETAMOL"},
            "Test notes"
        );
        
        assertTrue(result.contains("created"));
        
        List<Prescription> prescriptions = prescriptionController.getPatientPrescriptions("PAT001");
        assertFalse(prescriptions.isEmpty());
        
        
        assertTrue(prescriptions.get(0).getPrescriptionNumber().startsWith("RX"));
    }
    
    
    @Test
    public void testInventory() {
        prescriptionController.refillMedicine("PARACETAMOL", 10);
        //assertEquals(10, (int) prescriptionController.getInventory().get("PARACETAMOL"));
    }
    
    

} 