package sae.semestre.six.domain.billing;

import lombok.AllArgsConstructor;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
@AllArgsConstructor
public class BillingSecurityService {

    public record BillingSecurityDTO(byte[] salt, byte[] hash) {
    }

    private Environment environment;

    private final Charset usedCharset = StandardCharsets.UTF_8;
    private final int hashLength = 32;

    private Argon2Parameters argon2Parameters(byte[] salt) {
        byte[] secret = getSecret();
        int iterations = 2;
        int memLimit = 66536;
        int parallelism = 1;

        return new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(iterations)
                .withMemoryAsKB(memLimit)
                .withParallelism(parallelism)
                .withSalt(salt)
                .withSecret(secret)
                .build();
    }

    private byte[] getSecret() {
        String property = environment.getProperty("BILLING_HASH_SECRET");
        assert property != null;
        return property.getBytes(usedCharset);
    }

    private byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

        return salt;
    }

    public byte[] getAsBytesBase64(String data) {
        return Base64.getDecoder().decode(data.getBytes(usedCharset));
    }

    public BillingSecurityDTO generate(String input) {
        byte[] inputBytes = getAsBytesBase64(input);

        byte[] salt = generateSalt();
        Argon2Parameters parameters = argon2Parameters(salt);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(parameters);
        byte[] output = new byte[hashLength];
        generator.generateBytes(inputBytes, output, 0, output.length);

        return new BillingSecurityDTO(salt, output);
    }

    public boolean verify(String input, byte[] outputBytes, byte[] saltBytes) {
        byte[] inputBytes = getAsBytesBase64(input);
        Argon2Parameters parameters = argon2Parameters(saltBytes);

        Argon2BytesGenerator verifier = new Argon2BytesGenerator();
        verifier.init(parameters);
        byte[] testHash = new byte[hashLength];
        verifier.generateBytes(inputBytes, testHash, 0, testHash.length);
        return Arrays.equals(testHash, outputBytes);
    }
}
