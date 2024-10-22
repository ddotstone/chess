package dataaccess;

public class UnauthorizedException extends DataAccessException {
    private final int code = 401;

    public UnauthorizedException() {
        super("unauthorized");
    }

    public int getCode() {
        return code;
    }
}
