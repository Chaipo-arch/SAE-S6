package sae.semestre.six.exception;

/**
 * Exception pour indiquer à l'utilisateur des données invalides.
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
