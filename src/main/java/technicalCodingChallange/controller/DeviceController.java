package technicalCodingChallange.controller;

import org.springframework.web.bind.annotation.*;
import technicalCodingChallange.exceptions.BadRequestException;
import technicalCodingChallange.exceptions.InternalServerErrorException;
import technicalCodingChallange.exceptions.NotFoundException;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.service.DeviceService;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
private final DeviceService deviceService;

public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
}
    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("The device ID must not be null or empty.");
        }

        Device device = deviceService.findDeviceById(id);
        if (device == null) {
            throw new NotFoundException("Device with ID " + id + " not found.");
        }

        return device;
    }

    @PostMapping
    public Device createDevice(@RequestBody Device device) {
        if (device == null || device.getName() == null) {
            throw new BadRequestException("Device name must not be null.");
        }

        try {
            return deviceService.saveDevice(device);
        } catch (Exception e) {
            throw new InternalServerErrorException("An error occurred while creating the device.");
        }
    }


}
