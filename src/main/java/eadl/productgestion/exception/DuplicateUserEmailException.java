package eadl.productgestion.exception;

public class DuplicateUserEmailException extends BusinessException {

    public DuplicateUserEmailException(String email) {
        super("DUPLICATE_EMAIL", "L'adresse email est déjà utilisée : " + email);
    }
}
