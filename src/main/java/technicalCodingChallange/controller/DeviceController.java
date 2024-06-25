package technicalCodingChallange.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technicalCodingChallange.exceptions.BadRequestException;
import technicalCodingChallange.exceptions.InternalServerErrorException;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.service.DeviceService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
private final DeviceService deviceService;

public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
}

    @PostMapping
    public Device createDevice(@RequestBody Device device) {
        if (device == null || device.getName() == null) {
            throw new BadRequestException("Device name must not be null.");
        }
        try {
            return deviceService.addDevice(device);
        } catch (Exception e) {
            throw new InternalServerErrorException("An error occurred while creating the device.");
        }
    }

    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("The device ID must not be null or empty.");
        }
        return deviceService.getDeviceById(id);
    }

    @GetMapping
    public ResponseEntity<Page<Device>> getAllDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand) {

        Page<Device> devices = deviceService.getAllDevices(page, size, sortBy, name, brand);
        return ResponseEntity.ok(devices);
    }


}
