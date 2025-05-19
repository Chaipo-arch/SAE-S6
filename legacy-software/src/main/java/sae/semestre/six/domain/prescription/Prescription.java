package sae.semestre.six.domain.prescription;

import lombok.Getter;
import lombok.Setter;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.inventory.Inventory;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Entity representing a prescription in the hospital system.
 * Contains information about the patient, prescribed medicines, notes, cost, and audit fields.
 */
@Entity
@Getter
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique prescription number (e.g., RX123).
     */
    @Setter
    @Column(name = "prescription_number")
    private String prescriptionNumber; 

    /**
     * The patient to whom the prescription belongs.
     */
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @Setter
    private Patient patient;

    /**
     * List of medicines (inventory items) prescribed.
     */
    @Setter
    @ManyToMany
    @JoinTable(
        name = "prescription_medicines",
        joinColumns = @JoinColumn(name = "prescription_id"),
        inverseJoinColumns = @JoinColumn(name = "inventory_id")
    )
    private List<Inventory> medicines;

    /**
     * Additional notes for the prescription.
     */
    @Setter
    @Column(name = "notes")
    private String notes;

    /**
     * Total cost of the prescription.
     */
    @Setter
    @Column(name = "total_cost")
    private Double totalCost; 

    /**
     * Indicates if the prescription has been billed.
     */
    @Setter
    @Column(name = "is_billed")
    private Boolean isBilled = false; 

    /**
     * Indicates if the inventory has been updated for this prescription.
     */
    @Setter
    @Column(name = "inventory_updated")
    private Boolean inventoryUpdated = false; 

    /**
     * Date when the prescription was created.
     */
    @Setter
    @Column(name = "created_date")
    private Date createdDate = new Date();

    /**
     * Date when the prescription was last modified.
     */
    @Setter
    @Column(name = "last_modified")
    private Date lastModified = new Date();

    /**
     * Default constructor for JPA.
     */
    public Prescription() {}

    /**
     * Constructs a new Prescription with the given details.
     *
     * @param prescriptionId Unique prescription number
     * @param patient The patient
     * @param inventories List of prescribed medicines
     * @param notes Additional notes
     * @param cost Total cost
     */
    public Prescription(String prescriptionId, Patient patient, List<Inventory> inventories, String notes, double cost) {
        this.prescriptionNumber = prescriptionId;
        this.patient = patient;
        this.medicines = inventories;
        this.notes = notes;
        this.totalCost = cost;
    }

    /**
     * Calculates the total cost of this prescription, including a 20% markup.
     *
     * @return The total cost with markup, or 0.0 if medicines are not set
     */
    public Double getCostWithVat() {
        double total = 0.0;
        if (medicines != null) {
            for (Inventory inventory : medicines) {
                if (inventory != null) {
                    total += inventory.getUnitPrice();
                }
            }
        }
        return total * 1.2;
    }
}
