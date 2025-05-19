package sae.semestre.six.domain.billing;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.history.PatientHistory;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Représente une facture
 */
@Entity
@Table(name = "bills")
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
    @Setter
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BillDetail> billDetails = new HashSet<>();


    @Column(name = "created_date")
    private Date createdDate = new Date();

    @Column(name = "last_modified")
    private Date lastModified = new Date();

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
     * Modifie les détails de la facture et son coût total, en le calculant grâce à la liste des prix et les liste
     * des traitements associés passé en argument
     *
     * @param priceList  la liste des prix des traitements
     * @param treatments les traitements
     */
    public void calculateCost(Map<String, Double> priceList, String[] treatments) {
        final double TAUX_REDUCTION = 0.9;
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
            total = total * TAUX_REDUCTION;
        }

        this.setTotalAmount(total);
        this.setBillDetails(details);
    }
} 