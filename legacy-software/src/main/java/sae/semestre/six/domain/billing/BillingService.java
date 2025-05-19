package sae.semestre.six.domain.billing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;

import java.io.File;
import java.nio.file.Path;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * Service en charge des facturations
 */
@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingSecurityService billingSecurityService;
    @Getter
    private Map<String, Double> priceList = Map.of(
            "CONSULTATION", 50.0,
            "XRAY", 150.0,
            "SURGERY", 1000.0);

    @Getter
    @Value("${sae.semestre.six.files.billing}")
    private String BILLS_FOLDER;

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
    public void processBill(String patientId, String doctorId, String[] treatments) {
        // On récupère le patient et le docteur concerné
        Patient patient = patientDao.findById(Long.parseLong(patientId));
        Doctor doctor = doctorDao.findById(Long.parseLong(doctorId));

//        Hibernate.initialize(doctor.getAppointments());

        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);

        bill.calculateCost(priceList, treatments);

        String message = getBillDetailsForLogging(bill, Long.parseLong(patientId), Long.parseLong(doctorId), treatments);
        File file = Path.of(BILLS_FOLDER).resolve(bill.getBillNumber() + ".txt").toFile();
        fileHandler.writeToFile(file.getAbsolutePath(), message);

        BillingSecurityService.BillingSecurityDTO generated = billingSecurityService.generate(message);
        HexFormat format = HexFormat.of();
        String salt = format.formatHex(generated.salt());
        String hash = format.formatHex(generated.hash());
        bill.setHash(salt);
        bill.setHashSalt(hash);

        billDao.save(bill);

        sendEmailForNewBill(bill);
    }

    /**
     * Construit le message décrivant la nouvelle facture
     *
     * @param bill       la facture
     * @param patientId  l'identifiant du patient
     * @param doctorId   l'identifiant du docteur
     * @param treatments les traitements sur la facture
     */
    private String getBillDetailsForLogging(@NonNull Bill bill,
                                            @NonNull Long patientId,
                                            @NonNull Long doctorId,
                                            @NonNull String[] treatments) {
        if (bill.getBillNumber() == null) {
            throw new IllegalStateException("Bill number is null");
        }

        double total = 0.0;

        String billDetails = "Bill Number: " + bill.getBillNumber() + "\n";
        billDetails += "Patient: " + patientId + "\n";
        billDetails += "Doctor: " + doctorId + "\n";


        StringBuilder billDetailsBuilder = new StringBuilder(billDetails);
        for (String treatment : treatments) {
            double price = priceList.get(treatment);
            billDetailsBuilder.append(treatment).append(": $").append(price).append("\n");
        }
        billDetails = billDetailsBuilder.toString();


        billDetails += "Total: $" + total + "\n\n";
        return billDetails;
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
    private void updateBill(String billId, BillStatus status, String[] types) {
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
            updateBill(billId, BillStatus.RECALC, new String[]{"CONSULTATION"});
        }
    }

    List<String> getPendingBillsIds() {
        return billDao.findByStatus(BillStatus.PENDING)
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