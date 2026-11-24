package eadl.productgestion.exception;

public class DuplicateProductException extends BusinessException {

    public DuplicateProductException(String message) {
        super("DUPLICATE_PRODUCT", message);
    }
}
