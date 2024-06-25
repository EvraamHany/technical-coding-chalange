package technicalCodingChallange.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technicalCodingChallange.exceptions.BadRequestException;
import technicalCodingChallange.exceptions.InternalServerErrorException;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.service.DeviceService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
private final DeviceService deviceService;

public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
}

    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        if (device == null || device.getName() == null) {
            throw new BadRequestException("Device name must not be null.");
        }
        try {
            Device createdDevice = deviceService.addDevice(device);
            return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new InternalServerErrorException("An error occurred while creating the device.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("The device ID must not be null or empty.");
        }
        Device device = deviceService.getDeviceById(id);
        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device updatedDevice) {
        Device device = deviceService.updateDevice(id, updatedDevice);
        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Device> partiallyUpdateDevice(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Device device = deviceService.partiallyUpdateDevice(id, updates);
        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        boolean isDeleted = deviceService.deleteDevice(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<Device>> searchDevicesByBrand(@RequestParam String brand) {
        List<Device> devices = deviceService.searchDevicesByBrand(brand);
        return ResponseEntity.ok(devices);
    }

}
