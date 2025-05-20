package sae.semestre.six.domain.patient.history;

import sae.semestre.six.domain.patient.Patient;

import java.util.Date;

public record PatientHistoryInformation(Date visitDate, String diagnosis, String symptoms, String notes) {

    public PatientHistory toPatientHistory(Patient patient) {
        return PatientHistory.builder()
                .symptoms(symptoms).diagnosis(diagnosis)
                .notes(notes).visitDate(visitDate).patient(patient).build();
    }
}
