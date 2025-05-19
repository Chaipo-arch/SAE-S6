package sae.semestre.six.domain.patient.history;

import java.util.Date;

public record LabResultInformation(String testName, String resultValue, Date testDate, String notes) {

    public LabResult toLabResult() {
        return LabResult.builder().testName(testName)
                .notes(notes).resultValue(resultValue)
                .testDate(testDate).build();
    }
}
