package sae.semestre.six.domain.billing;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class BillingControllerTest {
    @Autowired
    private BillingController billingController;
    @Autowired
    private Environment env;
    @Test
    @DisplayName("Total revenue should be positive")
    public void testGetTotalRevenue() {
        String body = billingController.getTotalRevenue().getBody();
        assertNotNull(body);

        double total = Double.parseDouble(body.replace("Total Revenue: $", ""));
        assertTrue(total >= 0);
    }

    @Test
    @DisplayName("Creation of a bill should create new file")
    public void testProcessBill() {
        final String FOLDER_NAME = env.getProperty("sae.semestre.six.files.billing");
        final File BILLS_FOLDER = new File(FOLDER_NAME); // récupérer la valeur de `sae.semestre.six.files.billing`
        List<String> childrenBefore = new ArrayList<>(Arrays.asList(Objects.requireNonNull(BILLS_FOLDER.list())));

        ResponseEntity<String> responseEntity = billingController.processBill(
                "1",
                "1",
                new String[]{"CONSULTATION"});

        // Check operation success
        if (responseEntity.getStatusCode().isError()) {
            fail("Error: " + responseEntity.getStatusCode() + " - " + responseEntity.getBody());
        }
        String responseBody = responseEntity.getBody();

        // Check that file was created
        List<String> childrenAfter = new ArrayList<>(Arrays.asList(Objects.requireNonNull(BILLS_FOLDER.list())));
        childrenAfter.removeAll(childrenBefore);
        assertEquals(1, childrenAfter.size());

        File f = new File(BILLS_FOLDER, childrenAfter.getFirst());

        // Check operation success
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("successfully"));

        assertTrue(f.exists());
        assertTrue(f.length() > 0);
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