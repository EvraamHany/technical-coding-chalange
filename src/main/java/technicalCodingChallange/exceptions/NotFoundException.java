package technicalCodingChallange.exceptions;


public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(404, "404_NOT_FOUND", message);
    }
}
