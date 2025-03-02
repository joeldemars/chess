package service;

/**
 * 400 Bad Request
 */
public class BadRequestException extends HttpErrorException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
