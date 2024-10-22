package dataaccess;

public class NotFoundException extends DataAccessException {
    private final int code = 404;

    public NotFoundException() {
        super("page not found");
    }

    public int getCode() {
        return code;
    }
}
