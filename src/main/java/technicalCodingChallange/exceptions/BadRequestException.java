package technicalCodingChallange.exceptions;


public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(400, "400_BAD_REQUEST", message);
    }
}
