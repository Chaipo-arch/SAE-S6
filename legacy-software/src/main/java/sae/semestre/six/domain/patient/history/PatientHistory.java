package sae.semestre.six.domain.patient.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import sae.semestre.six.domain.billing.Bill;
import sae.semestre.six.domain.appointment.Appointment;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.prescription.Prescription;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "patient_history")
@AllArgsConstructor
@Builder
public class PatientHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "patient_id",nullable = false)
    private Patient patient;

    @Builder.Default
    @OneToMany(mappedBy = "patientHistory", fetch = FetchType.EAGER) 
    private Set<Appointment> appointments = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<Prescription> prescriptions = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<Treatment> treatments = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<Bill> bills = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<LabResult> labResults = new HashSet<>();
    
    @Column(name = "visit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitDate;
    
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(columnDefinition = "TEXT")
    private String symptoms;
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    public PatientHistory() {
        labResults = new HashSet<>();
        bills = new HashSet<>();
    }
    
    public Set<Appointment> getAppointments() {
        
        return new TreeSet<>(appointments);
    }

    public List<Bill> getBillsSorted() {
        
        List<Bill> sortedBills = new ArrayList<>(bills);
        Collections.sort(sortedBills, (b1, b2) -> b2.getBillDate().compareTo(b1.getBillDate()));
        return sortedBills;
    }


    public void addBill(Bill bill) {
        bills.add(bill);
        bill.setPatientHistory(this);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PatientHistory patientHistory) {
            return patientHistory.diagnosis.equals(diagnosis) && patientHistory.symptoms.equals(symptoms);
        }
        return false;
    }

    public Double getTotalBilledAmount() {
        return bills.stream()
            .mapToDouble(Bill::getTotalAmount)
            .sum();
    }


    public String getDiagnosis() {
        return diagnosis;
    }
}