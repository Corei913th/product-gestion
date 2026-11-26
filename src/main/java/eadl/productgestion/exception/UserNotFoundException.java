package eadl.productgestion.exception;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long id) {
        super("USER_NOT_FOUND", "Utilisateur introuvable : id=" + id);
    }

    public UserNotFoundException(String email) {
        super("USER_NOT_FOUND", "Utilisateur introuvable avec l'email : " + email);
    }
}
