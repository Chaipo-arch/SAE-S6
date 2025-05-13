package sae.semestre.six.domain.patient.history;

import org.junit.jupiter.api.Test;
import sae.semestre.six.domain.billing.Bill;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class PatientHistoryTest {

    @Test
    public void testGetTotalBilledAmount_WithMultipleBills() {
        PatientHistory history = new PatientHistory();

        Bill b1 = new Bill(); b1.setTotalAmount(100.0);
        Bill b2 = new Bill(); b2.setTotalAmount(200.0);
        history.addBill(b1);
        history.addBill(b2);

        double expectedTotal = (100.0 + 200.0);
        assertEquals(expectedTotal, history.getTotalBilledAmount(), 0.001);
    }

    @Test
    public void testGetBillsSorted_ShouldBeSortedDescending() {
        PatientHistory history = new PatientHistory();

        Bill b1 = new Bill();
        b1.setBillDate(dateFrom("2024-01-01"));
        Bill b2 = new Bill();
        b2.setBillDate(dateFrom("2024-04-01"));
        Bill b3 = new Bill();
        b3.setBillDate(dateFrom("2023-12-31"));

        history.addBill(b1);
        history.addBill(b2);
        history.addBill(b3);

        List<Bill> sorted = history.getBillsSorted();
        assertEquals(b2, sorted.get(0));
        assertEquals(b1, sorted.get(1));
        assertEquals(b3, sorted.get(2));
    }

    // Méthode utilitaire
    private Date dateFrom(String dateStr) {
        return java.sql.Date.valueOf(dateStr);
    }
}