package sae.semestre.six.domain.billing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.billing.medical_acts.MedicalAct;
import sae.semestre.six.domain.billing.medical_acts.MedicalActDao;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.prescription.Prescription;
import sae.semestre.six.domain.prescription.PrescriptionDao;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;

import java.io.File;
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
    private final MedicalActDao medicalActDao;
    private final PrescriptionDao prescriptionDao;
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
        Patient patient = patientDao.findById(Long.parseLong(patientId));
        Doctor doctor = doctorDao.findById(Long.parseLong(doctorId));
        this.processBill(patient, doctor, treatments);
    }

    /**
     * Génère une facture
     *
     * @param patient    le patient ayant été pris en charge
     * @param doctor     le doctor l'ayant pris en charge
     * @param treatments les traitements prescrits pour cette facture
     */
    @Transactional
    public void processBill(Patient patient, Doctor doctor, String[] treatments) {
        // On récupère le patient et le docteur concerné
        List<Billable> items = findBillablesByName(treatments);

        // Initialise la facture
        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);
        bill.setBillDetails(items);
        bill.calculateTotal();

        // Récupère et écrit les informations dans la facture
        String message = buildBillFileContents(bill,
                patient.getId(),
                doctor.getId(),
                treatments);
        File file = getFileForBillNumber(bill.getBillNumber());
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
     *
     * @param treatment le nom de l'acte médical
     * @param price     le prix (unitaire) de l'acte
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
     * Vérifie l'intégrité du fichier de facture
     *
     * @param billNumber le numéro de la facture
     * @return true si l'intégrité est préservée, false sinon
     */
    public boolean checkBillIntegrity(String billNumber) {
        // On récupère la facture correspondante
        Bill bill = billDao.findByBillNumber(billNumber);
        if (bill == null) {
            throw new NoSuchElementException("Bill number " + billNumber + " not found");
        }

        // Et le contenu du fichier
        String fileContent = getBillFileContents(bill);
        System.out.println("FILE CONTENT : " + fileContent);

        // On récupère le salt et le hash associé à la facture
        HexFormat format = HexFormat.of();
        byte[] salt = format.parseHex(bill.getHashSalt());
        byte[] hash = format.parseHex(bill.getHash());

        // On vérifie l'intégrité
        return billingSecurityService.verify(fileContent, hash, salt);
    }

    /**
     * @param bill la facture à récupérer
     * @return le contenu du fichier de facture
     */
    protected String getBillFileContents(@NonNull Bill bill) {
        File file = getFileForBillNumber(bill.getBillNumber());
        return fileHandler.readFromFile(file.getAbsolutePath());
    }

    /**
     * @param billNumber le numéro de facture
     * @return le fichier contenant (normalement) les informations de la facture
     */
    protected File getFileForBillNumber(@NonNull String billNumber) {
        return new File(BILLS_FOLDER, billNumber + ".txt");
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
    private List<Billable> findBillablesByName(@NonNull String[] treatments) {
        return new ArrayList<>(Arrays.asList(treatments))
                .stream()
                .map(this::findBillableByName)
                .toList();
    }

    /**
     * @param billableName le nom de l'élément facturable
     * @return le nom de l'élément facturable
     */
    private Billable findBillableByName(@NonNull String billableName) {
        boolean isPrescription = billableName.startsWith(Prescription.BILLABLE_PREFIX);

        if (isPrescription) {
            String prescriptionNumber = billableName.replace(Prescription.BILLABLE_PREFIX, "");
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
            total += price;
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