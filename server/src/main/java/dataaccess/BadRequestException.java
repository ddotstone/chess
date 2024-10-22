package dataaccess;

public class BadRequestException extends DataAccessException {
    private final int code = 400;

    public BadRequestException() {
        super("bad request");
    }

    public int getCode() {
        return code;
    }
}
