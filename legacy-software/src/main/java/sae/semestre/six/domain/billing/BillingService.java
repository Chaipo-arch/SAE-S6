package sae.semestre.six.domain.billing;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;

import java.util.*;

@Service
public class BillingService {

    private Map<String, Double> priceList = new HashMap<>();
    private List<String> pendingBills = new ArrayList<>();

    private final PatientDao patientDao;

    private final FileHandler fileHandler;
    private final BillDao billDao;

    private final EmailService emailService;

    private final DoctorDao doctorDao;

    public BillingService(PatientDao patientDao, FileHandler fileHandler, EmailService emailService, BillDao billDao, DoctorDao doctorDao) {
        this.patientDao = patientDao;
        this.fileHandler = fileHandler;
        this.billDao = billDao;
        this.emailService = emailService;
        this.doctorDao = doctorDao;
    }

    public String processBill(String patientId, String doctorId, String[] treatments) {
        try {
            Patient patient = patientDao.findById(Long.parseLong(patientId));
            Doctor doctor = doctorDao.findById(Long.parseLong(doctorId));

            Hibernate.initialize(doctor.getAppointments());

            Bill bill = new Bill();
            bill.setBillNumber("BILL" + System.currentTimeMillis());
            bill.setPatient(patient);
            bill.setDoctor(doctor);

            bill.calculateCost(priceList,treatments);

            fileHandler.writeToFile("C:\\hospital\\billing.txt",
                    bill.getBillNumber() + ": $" + bill.getTotalAmount() + "\n");

            billDao.save(bill);

            emailService.sendEmail(
                    EmailService.EMAIL_SOURCE.ADMIN.getEmail(),
                    "New Bill Generated",
                    "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + bill.getTotalAmount()
            );

            return "Bill processed successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private void updateBill(String billId,String status,String[] types) {
        Bill bill = billDao.findById(Long.valueOf(billId));
        bill.setStatus(status);
        billDao.update(bill);

    }

    public String updatePrice(
            String treatment,
            double price) {
        priceList.put(treatment, price);
        recalculateAllPendingBills();
        return "Price updated";
    }

    private void recalculateAllPendingBills() {
        for (String billId : pendingBills) {
            updateBill(billId, "RECALC", new String[]{"CONSULTATION"});
        }
    }

    public String getTotalRevenue() {
        return "Total Revenue: $"+ billDao.getTotalCost() ;
    }
} 