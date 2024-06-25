package technicalCodingChallange.exceptions;


public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(400, "400_BAD_REQUEST", message);
    }

    public BadRequestException(String reason, String message) {
        super(400, reason, message);
    }
}
