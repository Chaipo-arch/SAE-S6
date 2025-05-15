package sae.semestre.six.domain.patient.history;

import sae.semestre.six.domain.appointment.Appointment;
import sae.semestre.six.domain.billing.Bill;
import sae.semestre.six.domain.prescription.Prescription;

public record HistoryEntry(Appointment appointment, LabResult labResult, Treatment treatment, Bill bill, Prescription prescription) {
}
