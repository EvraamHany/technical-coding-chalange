package technicalCodingChallange.service;


import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import technicalCodingChallange.exceptions.BadRequestException;
import technicalCodingChallange.exceptions.DeviceValidationException;
import technicalCodingChallange.exceptions.NotFoundException;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.repository.DeviceRepo;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class DeviceServiceTest {

    @InjectMocks
    private DeviceService deviceService;

    @Mock
    private DeviceRepo deviceRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddDevice() {
        Device device = new Device("Device1", "Brand1");

        when(deviceRepo.findDeviceByName(device.getName())).thenReturn(Optional.empty());
        when(deviceRepo.save(device)).thenReturn(device);

        Device savedDevice = deviceService.addDevice(device);
        assertNotNull(savedDevice);
        assertEquals(device.getName(), savedDevice.getName());
    }

    @Test
    public void testAddDevice_DuplicateName() {
        Device device = new Device("Device1", "Brand1");
        when(deviceRepo.findDeviceByName(device.getName())).thenReturn(Optional.of(device));

        assertThrows(DeviceValidationException.class, () -> deviceService.addDevice(device));
    }

    @Test
    public void testAddDevice_ConstraintViolationException() {
        Device device = new Device("Device1", "Brand1");

        when(deviceRepo.findDeviceByName(device.getName())).thenReturn(Optional.empty());
        when(deviceRepo.save(device)).thenThrow(ConstraintViolationException.class);

        assertThrows(RuntimeException.class, () -> deviceService.addDevice(device));
    }

    @Test
    public void testGetDeviceById() {
        Device device = new Device("Device1", "Brand1");
        device.setId(1L);

        when(deviceRepo.findById(device.getId())).thenReturn(Optional.of(device));

        Device foundDevice = deviceService.getDeviceById(String.valueOf(device.getId()));
        assertNotNull(foundDevice);
        assertEquals(device.getName(), foundDevice.getName());
    }

    @Test
    public void testGetDeviceById_NotFound() {
        when(deviceRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deviceService.getDeviceById("1"));
    }

    @Test
    public void testGetDeviceById_InvalidIdFormat() {
        assertThrows(BadRequestException.class, () -> deviceService.getDeviceById("invalid-id"));
    }

    @Test
    public void testGetAllDevices() {
        List<Device> devices = Arrays.asList(new Device("Device1", "Brand1"), new Device("Device2", "Brand2"));
        Page<Device> devicePage = new PageImpl<>(devices);

        when(deviceRepo.findAll((Specification<Device>) any(), any(Pageable.class))).thenReturn(devicePage);

        Page<Device> result = deviceService.getAllDevices(0, 10, "name", "Device", "Brand");
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void testUpdateDevice() {
        Device device = new Device("Device1", "Brand1");
        device.setId(1L);

        when(deviceRepo.existsById(device.getId())).thenReturn(true);
        when(deviceRepo.save(device)).thenReturn(device);

        Device updatedDevice = deviceService.updateDevice(device.getId(), device);
        assertNotNull(updatedDevice);
        assertEquals(device.getName(), updatedDevice.getName());
    }

    @Test
    public void testUpdateDevice_NotFound() {
        when(deviceRepo.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> deviceService.updateDevice(1L, new Device("Device1", "Brand1")));
    }

    @Test
    public void testPartiallyUpdateDevice() {
        Device device = new Device("Device1", "Brand1");
        device.setId(1L);

        when(deviceRepo.findById(device.getId())).thenReturn(Optional.of(device));
        when(deviceRepo.save(device)).thenReturn(device);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "UpdatedDevice");

        Device updatedDevice = deviceService.partiallyUpdateDevice(device.getId(), updates);
        assertNotNull(updatedDevice);
        assertEquals("UpdatedDevice", updatedDevice.getName());
    }

    @Test
    public void testPartiallyUpdateDevice_NotFound() {
        when(deviceRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> deviceService.partiallyUpdateDevice(1L, new HashMap<>()));
    }

    @Test
    public void testDeleteDevice() {
        when(deviceRepo.existsById(anyLong())).thenReturn(true);

        boolean result = deviceService.deleteDevice(1L);
        assertTrue(result);
        verify(deviceRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteDevice_NotFound() {
        when(deviceRepo.existsById(anyLong())).thenReturn(false);

        boolean result = deviceService.deleteDevice(1L);
        assertFalse(result);
        verify(deviceRepo, never()).deleteById(anyLong());
    }

    @Test
    public void testSearchDevicesByBrand() {
        List<Device> devices = Arrays.asList(new Device("Device1", "Brand1"), new Device("Device2", "Brand1"));

        when(deviceRepo.findByBrandContainingIgnoreCase("Brand1")).thenReturn(devices);

        List<Device> result = deviceService.searchDevicesByBrand("Brand1");
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
