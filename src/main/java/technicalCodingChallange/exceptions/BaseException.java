package technicalCodingChallange.exceptions;

public class BaseException extends RuntimeException {
    private int statusCode;
    private String errorCode;

    public BaseException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
