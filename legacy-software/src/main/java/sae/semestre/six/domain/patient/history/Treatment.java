package sae.semestre.six.domain.patient.history;

import jakarta.persistence.*;
import lombok.Builder;

import java.util.Date;

@Entity
@Table(name = "treatments")
@Builder
public class Treatment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "patient_history_id",nullable = false)
    private PatientHistory patientHistory;
    
    @Column(name = "treatment_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date treatmentDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public PatientHistory getPatientHistory() { return patientHistory; }
    public void setPatientHistory(PatientHistory patientHistory) { this.patientHistory = patientHistory; }
    

} 