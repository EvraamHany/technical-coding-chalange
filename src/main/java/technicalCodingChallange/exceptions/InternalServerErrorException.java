package technicalCodingChallange.exceptions;


public class InternalServerErrorException extends BaseException {
    public InternalServerErrorException(String message) {
        super(500, "500_INTERNAL_SERVER_ERROR", message);
    }
}
