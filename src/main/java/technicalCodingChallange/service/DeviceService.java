package technicalCodingChallange.service;

import org.springframework.stereotype.Service;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.repository.DeviceRepo;

@Service
public class DeviceService {

    private final DeviceRepo deviceRepo;

    public DeviceService(DeviceRepo deviceRepo) {
        this.deviceRepo = deviceRepo;
    }

    public Device findDeviceById(String id) {
        return null;
    }

    public Device saveDevice(Device device) {
        return null;
    }
}
