package sae.semestre.six.domain.billing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.billing.medical_acts.MedicalAct;
import sae.semestre.six.domain.billing.medical_acts.MedicalActDaoImpl;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.prescription.PrescriptionDaoImpl;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service en charge des facturations
 */
@Service
@RequiredArgsConstructor
public class BillingService {

    @Value("${sae.semestre.six.files.billing}")
    private String BILLS_FOLDER;

    private final BillingSecurityService billingSecurityService;
    private final MedicalActDaoImpl medicalActDao;
    private final PrescriptionDaoImpl prescriptionDao;
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
     */
    @Transactional
    public void processBill(String patientId, String doctorId, String[] treatments) {
        // On récupère le patient et le docteur concerné
        Patient patient = patientDao.findById(Long.parseLong(patientId));
        Doctor doctor = doctorDao.findById(Long.parseLong(doctorId));
        List<Billable> items = findBillablesByName(treatments);

        // Initialise la facture
        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);
        bill.setBillDetails(items);
        bill.calculateTotal();

        // Récupère et écrit les informations dans la facture
        String message = buildBillFileContents(bill, Long.parseLong(patientId), Long.parseLong(doctorId), treatments);
        File file = Path.of(BILLS_FOLDER).resolve(bill.getBillNumber() + ".txt").toFile();
        fileHandler.writeToFile(file.getAbsolutePath(), message);

        // Génère un hash pour le contenu du fichier et l'ajoute aux informations de la facture
        BillingSecurityService.BillingSecurityDTO generated = billingSecurityService.generate(message);
        HexFormat format = HexFormat.of();
        String salt = format.formatHex(generated.salt());
        String hash = format.formatHex(generated.hash());
        bill.setHash(salt);
        bill.setHashSalt(hash);

        // Sauvegarde la facture
        billDao.save(bill);

        // Prévient de l'arrivée d'une nouvelle facture
        sendEmailForNewBill(bill);
    }

    /**
     * Met à jour le prix d'un traitement pour toutes les factures en attente
     *
     * @param treatment l'identifiant du traitement mis à jour
     * @param price     le nouveau prix du traitement
     */
    @Transactional
    public void updatePrice(
            String treatment,
            double price) {
        medicalActDao.updatePrice(treatment, price);
        recalculateAllPendingBills();
    }

    /**
     * @return la liste des prix des actes médicaux
     */
    public Map<String, Double> getPriceList() {
        return medicalActDao.findAll()
                .stream()
                .map(medicalAct -> Map.entry(medicalAct.getName(), medicalAct.getPrice()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Récupère le total du prix des factures
     *
     * @return une chaîne de caractères décrivant le total des factures
     */
    public double getTotalRevenue() {
        return billDao.getTotalCost();
    }

    /**
     * Ajoute le prix d'un acte médical
     * @param treatment le nom de l'acte médical
     * @param price le prix (unitaire) de l'acte
     */
    public void addPrice(String treatment, double price) {
        MedicalAct medicalAct = MedicalAct.builder().name(treatment).price(price).build();
        medicalActDao.save(medicalAct);
    }

    /**
     * @return les identifiants des factures en attente
     */
    public List<String> getPendingBillsIds() {
        return billDao.findByStatus(BillStatus.PENDING)
                .stream()
                .map(b -> b.getId().toString())
                .toList();
    }

    /**
     * Met à jour une facture en mettant à jour le statut
     *
     * @param billId l'identifiant de la facture
     * @param status le statut à appliquer sur la facture
     */
    @Transactional
    protected void updateBill(String billId, BillStatus status, String[] types) {
        Bill bill = billDao.findById(Long.valueOf(billId));
        bill.setStatus(status);
        billDao.update(bill);
    }

    /**
     * Recalcule les factures en attentes
     */
    @Transactional
    protected void recalculateAllPendingBills() {
        for (String billId : getPendingBillsIds()) {
            updateBill(billId, BillStatus.RECALC, new String[]{"CONSULTATION"});
        }
    }

    /**
     * @param treatments les traitements facturables
     * @return les éléments facturables par leurs noms
     */
    private List<Billable> findBillablesByName(String[] treatments) {
        return new ArrayList<>(Arrays.asList(treatments))
                .stream()
                .map(this::findBillableByName)
                .toList();
    }

    /**
     * @param billableName le nom de l'élément facturable
     * @return le nom de l'élément facturable
     */
    private Billable findBillableByName(String billableName) {
        final String prescriptionBeginning = "PRESCRIPTION_";
        boolean isPrescription = billableName.startsWith(prescriptionBeginning);

        if (isPrescription) {
            String prescriptionNumber = billableName.replace(prescriptionBeginning, "");
            return prescriptionDao.findByPrescriptionNumber(prescriptionNumber);
        } else {
            return medicalActDao.findByName(billableName);
        }
    }

    /**
     * Construit le message décrivant la nouvelle facture
     *
     * @param bill       la facture
     * @param patientId  l'identifiant du patient
     * @param doctorId   l'identifiant du docteur
     * @param treatments les traitements sur la facture
     */
    private String buildBillFileContents(@NonNull Bill bill,
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
            double price = medicalActDao.findPriceByName(treatment);
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
}