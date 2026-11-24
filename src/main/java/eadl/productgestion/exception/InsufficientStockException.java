package eadl.productgestion.exception;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(Long id, int requested, int available) {
        super("INSUFFICIENT_STOCK", "Stock insuffisant pour le produit id=" + id + " : demandé=" + requested + ", disponible=" + available);
    }
}
