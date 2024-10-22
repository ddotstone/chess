package dataaccess;

public class PermissionDeniedException extends DataAccessException {
    public PermissionDeniedException(String message) {
        super(message);
    }
}
