package sae.semestre.six.domain.billing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.Hibernate;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.history.PatientHistory;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "bills")
@Builder
@AllArgsConstructor
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bill_number", unique = true)
    private String billNumber;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @Column(name = "bill_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date billDate = new Date();
    
    @Column(name = "total_amount")
    private double totalAmount = 0.0;
    
    @Column(name = "status")
    private String status = "PENDING";
    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BillDetail> billDetails = new HashSet<>();
    
    
    @Column(name = "created_date")
    private Date createdDate = new Date();
    
    @Column(name = "last_modified")
    private Date lastModified = new Date();

    @ManyToOne
    private PatientHistory patientHistory;
    
    public Bill() {

    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBillNumber() { return billNumber; }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public Date getBillDate() { return billDate; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public void setStatus(String status) {
        this.status = status;
        this.lastModified = new Date(); 
    }
    
    public Set<BillDetail> getBillDetails() { return billDetails; }
    public void setBillDetails(Set<BillDetail> billDetails) { this.billDetails = billDetails; }

    public void calculateCost(Map<String, Double> priceList, String[] treatments) {
        double total = 0.0;
        Set<BillDetail> details = new HashSet<>();

        for (String treatment : treatments) {
            double price = priceList.get(treatment);
            total += price;

            BillDetail detail = new BillDetail();
            detail.setBill(this);
            detail.setTreatmentName(treatment);
            detail.setUnitPrice(price);
            details.add(detail);

            //Hibernate.initialize(detail); //TODO : vérifier utilité
        }

        if (total > 500) {
            total = total * 0.9;
        }

        this.setTotalAmount(total);
        this.setBillDetails(details);
    }


    public void setPatientHistory(PatientHistory patientHistory) {
        patientHistory = patientHistory;
    }
}