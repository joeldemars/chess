package api.exception;

/**
 * 403 Forbidden
 */
public class ForbiddenException extends HttpErrorException {
    public ForbiddenException(String message) {
        super(403, message);
    }
}
