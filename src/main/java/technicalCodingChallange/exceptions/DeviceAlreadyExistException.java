package technicalCodingChallange.exceptions;


public class DeviceAlreadyExistException extends BadRequestException {
    public DeviceAlreadyExistException(String message) {
        super("400_BAD_REQUEST_DEVICE_ALREADY_EXIST", message);
    }
}
