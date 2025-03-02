package service;

/**
 * 500: Internal Server Error
 */
public class InternalServerErrorException extends HttpErrorException {
    public InternalServerErrorException(String message) {
        super(500, message);
    }
}
