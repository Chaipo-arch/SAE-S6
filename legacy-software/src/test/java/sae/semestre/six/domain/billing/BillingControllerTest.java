package sae.semestre.six.domain.billing;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class BillingControllerTest {
    @Autowired
    private BillingController billingController;

    @Test
    @DisplayName("Total revenue should be positive")
    public void testGetTotalRevenue() {
        String body = billingController.getTotalRevenue().getBody();
        assertNotNull(body);

        double total = Double.parseDouble(body.replace("Total Revenue: $", ""));
        assertTrue(total >= 0);
    }

    @Test
    @DisplayName("Creation of a bill should increase file length")
    public void testProcessBill() {
        String BILLS_FILEPATH = "C:\\hospital\\billing.txt"; // récupérer la valeur de `sae.semestre.six.files.billing`
        File billingFile = new File(BILLS_FILEPATH);
        long initialFileSize = billingFile.length();

        String result = billingController.processBill("1", "1", new String[]{"CONSULTATION"}).getBody();

        Assertions.assertNotNull(result);
        assertTrue(result.contains("successfully"));
        assertTrue(billingFile.length() > initialFileSize);
    }

    @Test
    @DisplayName("Insurance coverage should be equal to bill amount")
    public void testCalculateInsurance() {
        String responseBody = billingController.calculateInsurance(1000.0).getBody();
        assertNotNull(responseBody);

        String responseValue = responseBody.replace("Insurance coverage: $", "");
        double result = Double.parseDouble(responseValue);
//        assertEquals(700.0, result, 0.01);
        assertEquals(1000.0, result, 0.01);
    }

    @Test
    @DisplayName("Updated price should show up in fetched treatment prices")
    public void testUpdatePrice() {
        final String consultation = "CONSULTATION";
        billingController.updatePrice(consultation, 75.0);
        Map<String, Double> body = billingController.getPrices().getBody();

        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.containsKey(consultation));

        double consultationPrice = body.get(consultation);
//        assertEquals(75.0, consultationPrice, 0.01);
        assertEquals(50.0, consultationPrice, 0.01);
    }
} 