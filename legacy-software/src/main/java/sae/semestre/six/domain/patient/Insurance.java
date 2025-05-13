package sae.semestre.six.domain.patient;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "insurance")
/**
 * Une assurance pour un patient.
 * Une assurance à une couverture et une couverture maximale.
 * La couverture fonctionne de cette manière :
 *      Si couverture = 10% et maxCouverture = 1000
 *      Si le billet médical est de 100 euros alors la couverture calculée est de 10 euros.
 *      Si le billet excéde 10000, par exemple 100000 euros alors la couverture calculée est de 1000 euros.
 */
public class Insurance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_number", unique = true,  nullable = false)
    private String policyNumber;
    
    @ManyToOne
    @JoinColumn(name = "patient_id",nullable = false)
    private Patient patient;
    
    @Column(name = "provider", nullable = false)
    private String provider;
    
    @Column(name = "coverage_percentage",nullable = false)
    private Double coveragePercentage;
    
    @Column(name = "max_coverage", nullable = false)
    private Double maxCoverage;
    
    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    public void setCoveragePercentage(Double coveragePercentage) {
        if(coveragePercentage < 0) {
            throw  new RuntimeException("Une assurance ne peut pas avoir une couverture négative.");
        }
        this.coveragePercentage = coveragePercentage;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setMaxCoverage(Double maxCoverage) {
        if(maxCoverage < 0) {
            throw  new RuntimeException("Une assurance ne peut pas avoir une couverture négative.");
        }
        this.maxCoverage = maxCoverage;
    }


    public Double calculateCoverage(Double billAmount) {
        Double coverage = billAmount * (coveragePercentage / 100);
        return coverage > maxCoverage ? maxCoverage : coverage;
    }
    
    
    public boolean isValid() {
        return new Date().before(expiryDate);
    }
} 