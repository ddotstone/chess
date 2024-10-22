package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    private final int code = 500;

    public DataAccessException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }
}

