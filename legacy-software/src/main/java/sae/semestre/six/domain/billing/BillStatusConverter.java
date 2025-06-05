package sae.semestre.six.domain.billing;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class BillStatusConverter implements AttributeConverter<BillStatus, String> {

    @Override
    public String convertToDatabaseColumn(BillStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    @Override
    public BillStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(BillStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}