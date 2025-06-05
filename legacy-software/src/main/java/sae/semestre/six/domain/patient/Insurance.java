package sae.semestre.six.domain.patient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import sae.semestre.six.exception.InvalidDataException;

import java.util.Date;

/**
 * Une assurance pour un patient.
 * Une assurance à une couverture et une couverture maximale.
 * La couverture fonctionne de cette manière :
 * Si couverture = 10% et maxCouverture = 1000
 * Si le billet médical est de 100 euros alors la couverture calculée est de 10 euros.
 * Si le billet excéde 1000, par exemple 100000 euros alors la couverture calculée est de 1000 euros.
 */
@Entity
@Table(name = "insurance")
@Builder(builderClassName = "InsuranceBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", unique = true, nullable = false)
    private String policyNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "coverage_percentage", nullable = false)
    private Double coveragePercentage;

    @Column(name = "max_coverage", nullable = false)
    private Double maxCoverage;

    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    public Double calculateCoverage(Double billAmount) {
        Double coverage = billAmount * (coveragePercentage / 100);
        return coverage > maxCoverage ? maxCoverage : coverage;
    }


    public boolean isValid() {
        return new Date().before(expiryDate);
    }

    public static class InsuranceBuilder {
        private Double maxCoverage;

        private Double coveragePercentage;
        private String policyNumber;
        private Date expiryDate;

        public InsuranceBuilder maxCoverage(Double maxCoverage) {
            if (maxCoverage == null) {
                throw new InvalidDataException("The insurance should have a max coverage");
            }
            if (maxCoverage < 0) {
                throw new InvalidDataException("An insurance can't have a max coverage negative");
            }
            this.maxCoverage = maxCoverage;
            return this;
        }

        public InsuranceBuilder coveragePercentage(Double coveragePercentage) {
            if (coveragePercentage == null) {
                throw new InvalidDataException("The insurance should have a coverage percentage");
            }
            if (coveragePercentage < 0) {
                throw new InvalidDataException("An insurance can't have a coverage percentage negative");
            }
            this.coveragePercentage = coveragePercentage;
            return this;
        }

        public InsuranceBuilder policyNumber(String policyNumber) {
            if (policyNumber == null) {
                throw new InvalidDataException("The insurance should have an policy number");
            }
            this.policyNumber = policyNumber;
            return this;
        }

        public InsuranceBuilder expiryDate(Date date) {
            if (date == null) {
                throw new InvalidDataException("The insurance should have an expiration date");
            }
            this.expiryDate = date;
            return this;
        }
    }

} 