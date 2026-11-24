package eadl.productgestion.dto.response;

public record FieldViolation(
        String field,
        String message
) {
}
