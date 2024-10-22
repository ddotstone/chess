package dataaccess;

public class AlreadyTakenException extends DataAccessException {
    private final int code = 403;

    public AlreadyTakenException() {
        super("already taken");
    }

    public int getCode() {
        return code;
    }
}
