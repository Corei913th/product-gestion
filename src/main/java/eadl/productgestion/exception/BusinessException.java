package eadl.productgestion.exception;

public abstract class BusinessException extends RuntimeException {

    private final String code;

    protected BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    protected BusinessException(String message) {
        this("BUSINESS_ERROR", message);
    }

    public String getCode() {
        return code;
    }
}
