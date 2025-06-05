package sae.semestre.six.domain.billing;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bill_details")
public class BillDetail {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @Setter
    @Column(name = "treatment_name")
    private String treatmentName;
    
    @Column(name = "quantity")
    private Integer quantity = 1;

    @Getter
    @Column(name = "unit_price")
    private Double unitPrice = 0.0;

    @Getter
    @Setter
    @Column(name = "line_total")
    private Double lineTotal = 0.0;
    
    
    public void calculateLineTotal() {
        this.lineTotal = this.quantity * this.unitPrice;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateLineTotal(); 
    }

    public void setUnitPrice(Double unitPrice) { 
        this.unitPrice = unitPrice;
        calculateLineTotal(); 
    }
} 