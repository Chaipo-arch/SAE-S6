package sae.semestre.six.domain.patient.history;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lab_results")
public class LabResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_history_id",nullable = false)
    private PatientHistory patientHistory;
    
    @Column(name = "test_name")
    private String testName;
    
    @Column(name = "result_value")
    private String resultValue;
    
    @Column(name = "test_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public PatientHistory getPatientHistory() { return patientHistory; }
    public void setPatientHistory(PatientHistory patientHistory) { this.patientHistory = patientHistory; }

} 