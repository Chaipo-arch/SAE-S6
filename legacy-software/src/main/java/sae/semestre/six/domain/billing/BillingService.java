package sae.semestre.six.domain.billing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;

import java.util.List;
import java.util.Map;

/**
 * Service en charge des facturations
 */
@Service
@RequiredArgsConstructor
public class BillingService {

    @Getter
    private Map<String, Double> priceList = Map.of(
            "CONSULTATION", 50.0,
            "XRAY", 150.0,
            "SURGERY", 1000.0);

    @Getter
    private final String BILLS_FILEPATH = "C:\\hospital\\billing.txt";

    private final PatientDao patientDao;
    private final FileHandler fileHandler;
    private final BillDao billDao;
    private final EmailService emailService;
    private final DoctorDao doctorDao;

    /**
     * Génère une facture
     *
     * @param patientId  l'identifiant du patient ayant été pris en charge
     * @param doctorId   l'identifiant du doctor l'ayant pris en charge
     * @param treatments les traitements prescrits pour cette facture
     * @return un message notifiant de la réussite ou l'échec de la création de la facture
     */
    @Transactional
    public String processBill(String patientId, String doctorId, String[] treatments) {
        try {
            Patient patient = patientDao.findById(Long.parseLong(patientId));
            Doctor doctor = doctorDao.findById(Long.parseLong(doctorId));

//            Hibernate.initialize(doctor.getAppointments());

            Bill bill = new Bill();
            bill.setBillNumber("BILL" + System.currentTimeMillis());
            bill.setPatient(patient);
            bill.setDoctor(doctor);

            bill.calculateCost(priceList, treatments);
            writeBillToFile(bill);

            billDao.save(bill);

            sendEmailForNewBill(bill);

            return "Bill processed successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Ajoute l'information de la facture dans le fichier
     *
     * @param bill la facture
     */
    private void writeBillToFile(Bill bill) {
        String textToAdd = bill.getBillNumber() + ": $" + bill.getTotalAmount() + "\n";
        fileHandler.writeToFile(BILLS_FILEPATH, textToAdd);
    }

    /**
     * Envoie un email à l'administrateur le notifiant de la nouvelle facture
     *
     * @param bill la nouvelle facture
     */
    private void sendEmailForNewBill(Bill bill) {
        String emailContent = "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + bill.getTotalAmount();
        emailService.sendEmail(
                EmailService.EMAIL_SOURCE.ADMIN.getEmail(),
                "New Bill Generated",
                emailContent
        );
    }

    /**
     * Met à jour une facture en mettant à jour le statut
     *
     * @param billId l'identifiant de la facture
     * @param status le statut à appliquer sur la facture
     */
    private void updateBill(String billId, String status, String[] types) {
        Bill bill = billDao.findById(Long.valueOf(billId));
        bill.setStatus(status);
        billDao.update(bill);
    }

    /**
     * Met à jour le prix d'un traitement pour toutes les factures en attente
     *
     * @param treatment l'identifiant du traitement mis à jour
     * @param price     le nouveau prix du traitement
     */
    public void updatePrice(
            String treatment,
            double price) {
        priceList.put(treatment, price);
        recalculateAllPendingBills();
    }

    /**
     * Recalcule les factures en attentes
     */
    private void recalculateAllPendingBills() {
        for (String billId : getPendingBillsIds()) {
            updateBill(billId, "RECALC", new String[]{"CONSULTATION"});
        }
    }

    List<String> getPendingBillsIds() {
        return billDao.findByStatus("PENDING")
                .stream()
                .map(b -> b.getId().toString())
                .toList();
    }

    /**
     * Récupère le total du prix des factures
     *
     * @return une chaîne de caractères décrivant le total des factures
     */
    public double getTotalRevenue() {
        return billDao.getTotalCost();
    }
} 