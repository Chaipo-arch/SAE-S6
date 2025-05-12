package sae.semestre.six.controller;

import org.junit.Test;
import sae.semestre.six.domain.billing.BillDaoImpl;
import sae.semestre.six.domain.billing.BillingController;
import sae.semestre.six.domain.billing.BillingService;
import sae.semestre.six.domain.doctor.DoctorDaoImpl;
import sae.semestre.six.domain.patient.PatientDaoImpl;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.GmailService;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

public class BillingControllerTest {
    
    
    private BillingController billingController = new BillingController(new BillingService(new PatientDaoImpl(),new FileHandler(),new GmailService(),new BillDaoImpl(),new DoctorDaoImpl()));
    
    @Test
    public void testProcessBill() {
        
        File billingFile = new File("C:\\hospital\\billing.txt");
        long initialFileSize = billingFile.length();
        
        String result = billingController.processBill(
            "TEST001",
            "DOC001",
            new String[]{"CONSULTATION"}
        ).toString();
        
        
        assertTrue(result.contains("successfully"));
        assertTrue(billingFile.length() > initialFileSize);
    }
    
    @Test
    public void testCalculateInsurance() {
        
        double result = Double.parseDouble(
            billingController.calculateInsurance(1000.0).toString()
                .replace("Insurance coverage: $", "")
        );
        
        
        assertEquals(700.0, result, 0.01);
    }
    
    
    @Test
    public void testUpdatePrice() {
        //billingController.updatePrice("CONSULTATION", 75.0);
        //assertEquals(75.0, billingController.getPrices().toString().("CONSULTATION"), 0.01);
    }
} 