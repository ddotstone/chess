package dataaccess;

public class EalreadyException extends DataAccessException {
    public EalreadyException(String message) {
        super(message);
    }
}
