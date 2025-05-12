package sae.semestre.six.domain.billing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;
import java.util.*;
import java.io.*;

import org.hibernate.Hibernate;
import sae.semestre.six.mail.GmailService;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    
    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }
    
    @PostMapping("/process")
    public ResponseEntity<String> processBill(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String[] treatments) {
        return ResponseEntity.ok(billingService.processBill(patientId,doctorId,treatments));
    }
    
    @PutMapping("/price")
    public ResponseEntity<String> updatePrice(
            @RequestParam String treatment,
            @RequestParam double price) {
        return ResponseEntity.ok(billingService.updatePrice(treatment,price));
    }
    

    @GetMapping("/prices")
    public ResponseEntity<Map<String, Double>> getPrices() {
        return ResponseEntity.ok(new HashMap<>()); //TODO get prices from bd
    }
    
    @GetMapping("/insurance")
    public ResponseEntity<String> calculateInsurance(@RequestParam double amount) {
        double coverage = amount; //TODO
        return ResponseEntity.ok("Insurance coverage: $" + coverage);
    }
    
    @GetMapping("/revenue")
    public ResponseEntity<String> getTotalRevenue() {
        return ResponseEntity.ok(billingService.getTotalRevenue());
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<String>> getPendingBills() {
        return ResponseEntity.ok(new ArrayList<>()); //TODO
    }
} 