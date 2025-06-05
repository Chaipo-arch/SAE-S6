package sae.semestre.six.domain.patient.history;

import java.util.List;

/**
 * Sommaire d'un patient.
 * @param visitCount Le nombre de visite fait par le patient.
 * @param totalBill Le total montant que le patient a dépensé.
 */
public record PatientSummary(int visitCount, double totalBill) {

    public static PatientSummary from(List<PatientHistory> histories) {
        return new PatientSummary(
                histories.size(),
                histories.stream().mapToDouble(PatientHistory::getTotalBilledAmount).sum()
        );
    }
}
