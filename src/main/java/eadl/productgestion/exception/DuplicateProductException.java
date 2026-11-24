package eadl.productgestion.exception;

public class DuplicateProductException extends BusinessException {

    public DuplicateProductException(String sku) {
        super("DUPLICATE_SKU", "La référence (SKU) existe déjà : " + sku);
    }
}
