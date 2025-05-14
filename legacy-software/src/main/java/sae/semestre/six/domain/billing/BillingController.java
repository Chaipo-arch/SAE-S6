package sae.semestre.six.domain.billing;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;

    /**
     * Génère une facture
     *
     * @param patientId  l'identifiant du patient ayant été pris en charge
     * @param doctorId   l'identifiant du doctor l'ayant pris en charge
     * @param treatments les traitements prescrits pour cette facture
     * @return un message notifiant de la réussite ou l'échec de la création de la facture
     */
    @PostMapping("/process")
    public ResponseEntity<String> processBill(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String[] treatments) {
        return ResponseEntity.ok(billingService.processBill(patientId, doctorId, treatments));
    }

    /**
     * Met à jour le prix d'un acte médical
     *
     * @param treatment l'acte médical
     * @param price     le nouveau prix
     * @return un message positif ou négatif en fonction de la réussite
     */
    @PutMapping("/price")
    public ResponseEntity<String> updatePrice(
            @RequestParam String treatment,
            @RequestParam double price) {
        try {
            billingService.updatePrice(treatment, price);
            return ResponseEntity.ok("Price updated");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * @return les actes médicaux et leurs prix
     */
    @GetMapping("/prices")
    public ResponseEntity<Map<String, Double>> getPrices() {
        return ResponseEntity.ok(billingService.getPriceList());
    }

    /**
     * Calcule le remboursement par l'assurance
     *
     * @param amount le montant de la facture
     * @return le montant remboursé
     */
    @GetMapping("/insurance")
    public ResponseEntity<String> calculateInsurance(@RequestParam double amount) {
        // TODO calculer le remboursement avec ms-assurance via une architecture micro-services
        // A utiliser : webClient (dépendance légère) ou Feign (un peu lourdingue)
        // pour webClient, voir service adresse dans la SAE du S5
        double coverage = amount;
        return ResponseEntity.ok("Insurance coverage: $" + coverage);
    }

    /**
     * @return le total du montant des factures
     */
    @GetMapping("/revenue")
    public ResponseEntity<String> getTotalRevenue() {
        return ResponseEntity.ok("Total Revenue: $" + billingService.getTotalRevenue());
    }

    /**
     * @return les identifiants des factures en attente
     */
    @GetMapping("/pending")
    public ResponseEntity<List<String>> getPendingBills() {
        return ResponseEntity.ok(billingService.getPendingBillsIds());
    }
} 