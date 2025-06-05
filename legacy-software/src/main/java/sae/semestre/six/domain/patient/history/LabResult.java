package sae.semestre.six.domain.patient.history;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "lab_results")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResult {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "patient_history_id", nullable = false)
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
} 