package sae.semestre.six.domain.billing;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class BillingSecurityServiceTest {
    @Autowired
    private BillingSecurityService billingSecurityService;

    @Test
    public void givenRawPasswordAndSalt_whenArgon2AlgorithmIsUsed_thenHashIsCorrect() {
        String input = "password";
        BillingSecurityService.BillingSecurityDTO output = billingSecurityService.generate(input);
        assertTrue(billingSecurityService.verify(input, output.hash(), output.salt()));
    }
} 