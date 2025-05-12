package sae.semestre.six.domain.prescription;

import sae.semestre.six.domain.inventory.Inventory;
import sae.semestre.six.domain.inventory.InventoryController;
import sae.semestre.six.domain.inventory.InventoryDao;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.billing.BillingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.file.FileHandler;

import java.util.*;
import java.io.*;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final FileHandler fileHandler;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    InventoryController inventoryController;
    @Autowired
    private BillingService billingService;
    
    
    private static final Map<String, Double> medicinePrices = new HashMap<String, Double>() {{
        put("PARACETAMOL", 5.0);
        put("ANTIBIOTICS", 25.0);
        put("VITAMINS", 15.0);
    }};

    private static final String AUDIT_FILE = "C:\\hospital\\prescriptions.log";
    
    
    @Autowired
    private PatientDao patientDao;
    
    @Autowired
    private PrescriptionDao prescriptionDao;

    public PrescriptionController(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @PostMapping("/add")
    public String addPrescription(
            @RequestParam String patientId,
            @RequestParam String[] medicines,
            @RequestParam String notes) {
        try {
            int prescriptionCounter =prescriptionDao.getNumberOfPrescriptions();
            Inventory[] inventories = new Inventory[medicines.length];
            double cost = 0;
            for(int i = 0; i< medicines.length ; i++) {
                Inventory inventory = inventoryDao.findByItemCode(medicines[i]);

                if(inventory.needsRestock()) {
                    // TODO vérifier si le stock doit être non vide.
                }
                cost += inventory.getUnitPrice();
                inventories[i] = inventory;

            }
            prescriptionCounter++;
            String prescriptionId = "RX" + prescriptionCounter;
            
            Prescription prescription = new Prescription();
            prescription.setPrescriptionNumber(prescriptionId);
            
            Patient patient = patientDao.findById(Long.parseLong(patientId));
            prescription.setPatient(patient);
            
            prescription.setMedicines(String.join(",", medicines));
            prescription.setNotes(notes);

            prescription.setTotalCost(cost);

            prescriptionDao.save(prescription);

            fileHandler.writeToFile(AUDIT_FILE,
                    new Date().toString() + " - " + prescriptionId + "\n");
            
            billingService.processBill(
                patientId,
                "SYSTEM",
                new String[]{"PRESCRIPTION_" + prescriptionId}
            );

            for (Inventory inventory : inventories) {
                inventory.setQuantity(inventory.getQuantity() -1);
            }
            
            return "Prescription " + prescriptionId + " created and billed";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed: " + e.toString();
        }
    }
    
    @GetMapping("/patient/{patientId}")
    public List<Prescription> getPatientPrescriptions(@PathVariable String patientId) {
        
        return prescriptionDao.findByPatientId(Long.parseLong(patientId));
    }
    
    @PostMapping("/refill")
    public String refillMedicine(
            @RequestParam String medicine,
            @RequestParam int quantity) {

        inventoryController.refillStock(medicine,quantity);
        return "Refilled " + medicine;
    }

    public double calculateCost(@PathVariable String prescriptionId) {

        return medicinePrices.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum() * 1.2;
    }

} 