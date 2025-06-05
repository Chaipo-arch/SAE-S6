package sae.semestre.six.domain.patient.history;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "treatments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "patient_history_id", nullable = false)
    private PatientHistory patientHistory;

    @Column(name = "treatment_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date treatmentDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
} 