package technicalCodingChallange.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import technicalCodingChallange.exceptions.BadRequestException;
import technicalCodingChallange.exceptions.DeviceValidationException;
import technicalCodingChallange.exceptions.InternalServerErrorException;
import technicalCodingChallange.exceptions.NotFoundException;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.repository.DeviceRepo;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeviceService {

    private final DeviceRepo deviceRepo;

    public DeviceService(DeviceRepo deviceRepo) {
        this.deviceRepo = deviceRepo;
    }

    public Device addDevice(Device device) {
        validateDevice(device);

        try {
            Optional<Device> existingDevice = deviceRepo.findDeviceByName(device.getName());
            if (existingDevice.isPresent()) {
                throw new DeviceValidationException("Device with the same name already exists");
            }

            return deviceRepo.save(device);
        } catch (ConstraintViolationException e) {
            throw new RuntimeException("Failed to save device due to constraint violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to save device due to database error", e);
        }
    }

    public Device getDeviceById(String id) {
        try {
            Optional<Device> device = deviceRepo.findById(Long.parseLong(id));
            if (device.isPresent()) {
                return device.get();
            } else {
                throw new NotFoundException("Device with ID " + id + " not found.");
            }
        }catch (NumberFormatException e) {
            throw new BadRequestException("invalid id format for device");
        }
    }

    public Page<Device> getAllDevices(int page, int size, String sortBy, String name, String brand) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

            return deviceRepo.findAll((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (name != null && !name.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
                }
                if (brand != null && !brand.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("brand"), "%" + brand + "%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }, pageable);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid pagination or sorting parameters");
        } catch (DataAccessException | PersistenceException e) {
            throw new InternalServerErrorException("Failed to access the database");
        }
    }

    public Device updateDevice(Long id, Device updatedDevice) {
        if (!deviceRepo.existsById(id)) {
            throw new EntityNotFoundException("Device not found");
        }
        updatedDevice.setId(id);
        return deviceRepo.save(updatedDevice);
    }

    public Device partiallyUpdateDevice(Long id, Map<String, Object> updates) {
        Device device = deviceRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Device not found"));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Device.class, key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, device, value);
        });

        return deviceRepo.save(device);
    }

    public boolean deleteDevice(Long id) {
        if (deviceRepo.existsById(id)) {
            deviceRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Device> searchDevicesByBrand(String brand) {
        return deviceRepo.findByBrandContainingIgnoreCase(brand);
    }



    private void validateDevice(Device device) {
        if (device == null) {
            throw new DeviceValidationException("Device cannot be null");
        }
        if (device.getName() == null || device.getName().trim().isEmpty()) {
            throw new DeviceValidationException("Device name cannot be null or empty");
        }
        if (device.getBrand() == null || device.getBrand().trim().isEmpty()) {
            throw new DeviceValidationException("Device brand cannot be null or empty");
        }
        if (device.getCreationTime() == null) {
            device.setCreationTime(LocalDateTime.now());
        }
    }

}
