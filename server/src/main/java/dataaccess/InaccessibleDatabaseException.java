package dataaccess;

public class InaccessibleDatabaseException extends DataAccessException {
    public InaccessibleDatabaseException(String message) {
        super(message);
    }
}
