package sae.semestre.six.domain.billing;

import jakarta.persistence.*;
import lombok.*;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.history.PatientHistory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Représente une facture
 */
@Entity
@Table(name = "bills")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "bill_number", unique = true)
    private String billNumber;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Getter
    @Setter
    @Column(name = "bill_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date billDate = new Date();

    @Getter
    @Setter
    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    @Column(name = "status")
    @Convert(converter = BillStatusConverter.class)
    private BillStatus status = BillStatus.PENDING;

    @Getter
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BillDetail> billDetails = new HashSet<>();

    @Column(name = "created_date")
    private Date createdDate = new Date();

    @Column(name = "last_modified")
    private Date lastModified = new Date();

    @Getter
    @Setter
    @ManyToOne
    private PatientHistory patientHistory;

    @Getter
    @Setter
    private String hashSalt;

    @Getter
    @Setter
    private String hash;

    public void setStatus(BillStatus status) {
        this.status = status;
        this.lastModified = new Date();
    }

    /**
     * Ajoute les détails de la facture
     * @param billables les billables de la facture
     */
    public void setBillDetails(List<Billable> billables) {
        Set<BillDetail> details = new HashSet<>();
        for (Billable billable : billables) {
            BillDetail detail = new BillDetail();
            detail.setBill(this);
            detail.setTreatmentName(billable.getBillableName());
            detail.setUnitPrice(billable.getBillableAmount());
            details.add(detail);
        }
        this.setBillDetails(details);
    }

    /**
     * @param details les détails de la facture
     */
    public void setBillDetails(Set<BillDetail> details) {
        this.billDetails = details;
    }

    /**
     * Calcule et remplit le total de la facture
     */
    public void calculateTotal() {
        final double TAUX_REDUCTION = 0.9;
        final double SEUIL_REDUCTION = 500.0;
        double total = 0.0;

        for (BillDetail item : this.getBillDetails()) {
            total += item.getLineTotal();
        }

        if (total > SEUIL_REDUCTION) {
            total = total * TAUX_REDUCTION;
        }
        this.setTotalAmount(total);
    }
}