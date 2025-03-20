package api.exception;

/**
 * 401 Unauthorized
 */
public class UnauthorizedException extends HttpErrorException {
    public UnauthorizedException(String message) {
        super(401, message);
    }
}
