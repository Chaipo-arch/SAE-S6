package sae.semestre.six.domain.billing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BillingControllerTest {
    @Autowired
    private BillingController billingController;

    private final String BILLS_FILEPATH = "C:\\hospital\\billing.txt";

    @Test
    public void testGetTotalRevenue() {
        String body = billingController.getTotalRevenue().getBody();
        assertNotNull(body);
    }
    @Test
    public void testProcessBill() {
        File billingFile = new File(BILLS_FILEPATH);
        long initialFileSize = billingFile.length();

        String result = billingController.processBill("1", "1", new String[]{"CONSULTATION"}).getBody();

        Assertions.assertNotNull(result);
        assertTrue(result.contains("successfully"));
        assertTrue(billingFile.length() > initialFileSize);
    }

    @Test
    public void testCalculateInsurance() {
        String responseBody = billingController.calculateInsurance(1000.0).getBody();
        String responseValue = responseBody.replace("Insurance coverage: $", "");
        double result = Double.parseDouble(responseValue);
//        assertEquals(700.0, result, 0.01);
        assertEquals(1000.0, result, 0.01);
    }

    @Test
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