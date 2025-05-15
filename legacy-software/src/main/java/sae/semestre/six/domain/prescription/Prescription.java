package sae.semestre.six.domain.prescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import sae.semestre.six.domain.patient.Patient;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "prescriptions")
public class Prescription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "prescription_number")
    private String prescriptionNumber; 
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @Column(name = "medicines")
    private String medicines; 
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "total_cost")
    private Double totalCost; 
    
    @Column(name = "is_billed")
    private Boolean isBilled = false; 
    
    @Column(name = "inventory_updated")
    private Boolean inventoryUpdated = false; 
    
    
    @Column(name = "created_date")
    private Date createdDate = new Date();
    
    @Column(name = "last_modified")
    private Date lastModified = new Date();
    
    
    public Prescription() {}
    
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}