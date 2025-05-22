package sae.semestre.six.domain.inventory;

import java.text.SimpleDateFormat;

/**
 * Data Transfer Object (DTO) for PriceHistory entity.
 * Encapsulates price change details for API communication.
 */
public record PriceHistoryDTO(Double oldPrice, Double newPrice, String changeDate) {
    /**
     * Constructs a PriceHistoryDTO from a PriceHistory entity.
     *
     * @param ph The PriceHistory entity
     */
    public PriceHistoryDTO(PriceHistory ph) {
        this(
            ph.getOldPrice(),
            ph.getNewPrice(),
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ph.getChangeDate())
        );
    }
}
