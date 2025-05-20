package sae.semestre.six.domain.patient.history;

import java.util.Date;

/**
 * DTO (Data Transfer Object) utilisé pour transférer les données nécessaires à la création
 * d'un objet {@link LabResult}. Cette classe permet d'encapsuler les informations
 * d’un résultat de laboratoire avant leur transformation en entité métier.
 *
 * @param testName nom du test effectué (ex. : "Glycémie")
 * @param resultValue valeur ou résultat du test (ex. : "5.4 mmol/L")
 * @param testDate date à laquelle le test a été réalisé
 * @param notes remarques ou observations éventuelles associées au test
 */
public record LabResultInformation(String testName, String resultValue, Date testDate, String notes) {

    public LabResult toLabResult() {
        return LabResult.builder().testName(testName)
                .notes(notes).resultValue(resultValue)
                .testDate(testDate).build();
    }
}
