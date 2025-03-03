package service.exception;

public abstract class HttpErrorException extends RuntimeException {
    public int status;

    public HttpErrorException(int status, String message) {
        super(message);
        this.status = status;
    }
}
