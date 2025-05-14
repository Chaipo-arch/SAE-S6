package sae.semestre.six.domain.patient;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

public class InsuranceTest {

    public final Insurance INSURANCE = Insurance.builder().expiryDate(addDaysToDate(new Date(), 10)).maxCoverage(200.0).coveragePercentage(90.0).policyNumber("Policy").build();

    @Test
    public void testCalculateCoverageUnderMaxCoverage() {
        Insurance insurance = createInsurance(80.0, 1000.0);
        Double bill = 500.0;
        Double expectedCoverage = 500.0 * 0.8;

        assertEquals(expectedCoverage, insurance.calculateCoverage(bill));
    }

    @Test
    public void testCoverage0percent() {
        Insurance insurance = createInsurance(0.0,0.0);
        double coverageTest = insurance.calculateCoverage(0.0);
        assertEquals(0,coverageTest,"La couverture d'une assurance à 0% doit donner 0.");

    }

    @Test
    public void testBuildCoverageWithNegatives() {
        assertThrows(RuntimeException.class,()->createInsurance(-10.0,-100.0),"Une assurance avec une couverture négative ne peut pas être créée.");
        assertThrows(RuntimeException.class,()->createInsurance(10.0,-100.0),"Une assurance avec une couverture maximale négative ne peut pas être créée.");
    }

    @Test
    public void testCalculateCoverageNegativeValue() {
        Insurance insurance = createInsurance(80.0, 1000.0);
        Double bill = -500.0;
        Double coverage = insurance.calculateCoverage(bill);

        assertTrue(coverage <= 0, "La couverture ne devrait pas être positive si les valeurs sont négatives");
    }

    @Test
    public void testCalculateCoverage_ExceedsMaxCoverage() {
        Double bill = 500.0;
        // 500 * 90% = 450 > max 200
        assertEquals(200.0, INSURANCE.calculateCoverage(bill));
    }

    @Test
    public void testIsValid_True() {
        assertTrue(INSURANCE.isValid(), "L'assurance devrait être valide dans le futur");
    }

    @Test
    public void testIsValid_False() {
        Insurance insurance = Insurance.builder().expiryDate(addDaysToDate(new Date(), -5)).maxCoverage(200.0).coveragePercentage(90.0).policyNumber("Policy").build();


        assertFalse(insurance.isValid(), "L'assurance expirée ne devrait pas être valide");
    }

    // Méthodes utilitaires
    private Insurance createInsurance(Double coveragePercentage, Double maxCoverage) {
        Insurance insurance =  Insurance.builder().expiryDate(new Date()).maxCoverage(maxCoverage).coveragePercentage(coveragePercentage).policyNumber("dsfgh").build();
        return insurance;
    }

    private Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
}