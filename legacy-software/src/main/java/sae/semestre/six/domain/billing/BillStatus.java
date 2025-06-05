package sae.semestre.six.domain.billing;

public enum BillStatus {
    PAID,
    RECALC,
    PENDING;

    public String getCode() {
        return switch (this) {
            case PAID -> "PAYÉE";
            case RECALC -> "RECALCUL";
            case PENDING -> "EN ATTENTE";
        };
    }
}

