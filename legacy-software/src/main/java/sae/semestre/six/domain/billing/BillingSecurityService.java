package sae.semestre.six.domain.billing;

import lombok.AllArgsConstructor;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Service en charge de la sécurité des fichiers de factures
 */
@Service
@AllArgsConstructor
public class BillingSecurityService {

    public record BillingSecurityDTO(byte[] salt, byte[] hash) {
    }

    private Environment environment;

    private final Charset usedCharset = StandardCharsets.UTF_8;
    private final int hashLength = 32;

    /**
     * Génère les paramètres pour l'algorithme Argon2
     * @param salt le sel à utiliser
     * @return les paramètres pour l'algorithme Argon2
     */
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

    /**
     * @return le secret utilisé pour générer le hash
     */
    private byte[] getSecret() {
        String property = environment.getProperty("BILLING_HASH_SECRET");
        assert property != null;
        return property.getBytes(usedCharset);
    }

    /**
     * Génère un sel pour renforcer le hash
     * @return un sel aléatoire
     */
    private byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

        return salt;
    }

    /**
     * Récupère les octets d'une chaîne de caractères en base64
     * @param data la chaîne de caractère
     * @return ses octets en base64
     */
    public byte[] getAsBytesBase64(String data) {
        return Base64.getEncoder().encode(data.getBytes(usedCharset));
    }

    /**
     * Génère un sel et un hash avec Argon2 pour la chaîne en entrée
     * @param input la donnée d'entrée
     * @return un sel et un hash pour la chaîne en entrée
     */
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

    /**
     * Vérifie la correspondance d'un hash avec sa chaîne de départ
     * @param input la chaîne d'entrée
     * @param outputBytes les données en sortie
     * @param saltBytes le sel donné en entrée
     * @return true si les données correspondent, false sinon
     */
    public boolean verify(String input, byte[] outputBytes, byte[] saltBytes) {
        byte[] inputBytes = getAsBytesBase64(input);
        Argon2Parameters parameters = argon2Parameters(saltBytes);

        Argon2BytesGenerator verifier = new Argon2BytesGenerator();
        verifier.init(parameters);
        byte[] outputReprocessedBytes = new byte[hashLength];
        verifier.generateBytes(inputBytes, outputReprocessedBytes, 0, outputReprocessedBytes.length);
        return Arrays.equals(outputReprocessedBytes, outputBytes);
    }
}
