package technicalCodingChallange.exceptions;


public class DeviceValidationException extends BadRequestException {
    public DeviceValidationException(String message) {
        super("400_BAD_REQUEST_DEVICE_VALIDATION", message);
    }
}
