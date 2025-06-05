package sae.semestre.six.domain.billing.medical_acts;

import jakarta.persistence.*;
import lombok.*;
import sae.semestre.six.domain.billing.Billable;

@Entity
@Table(name = "medical_act",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalAct implements Billable {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String name;

    @Getter
    private double price;

    @Override
    public double getBillableAmount() {
        return getPrice();
    }

    @Override
    public String getBillableName() {
        return getName();
    }
}
