package technicalCodingChallange.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import technicalCodingChallange.exceptions.BadRequestException;
import technicalCodingChallange.exceptions.InternalServerErrorException;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.service.DeviceService;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceControllerTest {

    @InjectMocks
    private DeviceController deviceController;

    @Mock
    private DeviceService deviceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDevice() {
        Device device = new Device("iphone 13", "apple");

        when(deviceService.addDevice(any(Device.class))).thenReturn(device);

        ResponseEntity<Device> response = deviceController.createDevice(device);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(device.getName(), response.getBody().getName());
    }

    @Test
    public void testCreateDevice_NullDevice() {
        assertThrows(BadRequestException.class, () -> deviceController.createDevice(null));
    }

    @Test
    public void testCreateDevice_NullDeviceName() {
        Device device = new Device(null, "apple");
        assertThrows(BadRequestException.class, () -> deviceController.createDevice(device));
    }

    @Test
    public void testCreateDevice_InternalServerError() {
        Device device = new Device("iphone 13", "apple");

        when(deviceService.addDevice(any(Device.class))).thenThrow(RuntimeException.class);

        assertThrows(InternalServerErrorException.class, () -> deviceController.createDevice(device));
    }

    @Test
    public void testGetDeviceById() {
        Device device = new Device("iphone 13", "apple");
        device.setId(1L);

        when(deviceService.getDeviceById("1")).thenReturn(device);

        ResponseEntity<Device> response = deviceController.getDeviceById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(device.getName(), response.getBody().getName());
    }

    @Test
    public void testGetDeviceById_NotFound() {
        when(deviceService.getDeviceById("1")).thenReturn(null);

        ResponseEntity<Device> response = deviceController.getDeviceById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetDeviceById_NullOrEmptyId() {
        assertThrows(BadRequestException.class, () -> deviceController.getDeviceById(null));
        assertThrows(BadRequestException.class, () -> deviceController.getDeviceById(""));
    }

    @Test
    public void testGetAllDevices() {
        List<Device> devices = Arrays.asList(new Device("iphone 13", "apple"), new Device("A 15", "samsung"));
        Page<Device> devicePage = new PageImpl<>(devices);

        when(deviceService.getAllDevices(0, 10, "id", null, null)).thenReturn(devicePage);

        ResponseEntity<Page<Device>> response = deviceController.getAllDevices(0, 10, "id", null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    public void testUpdateDevice() {
        Device device = new Device("iphone 13", "apple");
        device.setId(1L);

        when(deviceService.updateDevice(anyLong(), any(Device.class))).thenReturn(device);

        ResponseEntity<Device> response = deviceController.updateDevice(1L, device);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(device.getName(), response.getBody().getName());
    }

    @Test
    public void testUpdateDevice_NotFound() {
        when(deviceService.updateDevice(anyLong(), any(Device.class))).thenReturn(null);

        ResponseEntity<Device> response = deviceController.updateDevice(1L, new Device("iphone 13", "apple"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testPartiallyUpdateDevice() {
        Device existingDevice = new Device("iphone 13", "apple");
        existingDevice.setId(1L);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "UpdatedDevice");

        Device updatedDevice = new Device("UpdatedDevice", "apple");
        updatedDevice.setId(1L);

        when(deviceService.partiallyUpdateDevice(anyLong(), anyMap())).thenReturn(updatedDevice);

        ResponseEntity<Device> response = deviceController.partiallyUpdateDevice(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UpdatedDevice", response.getBody().getName());
    }

    @Test
    public void testPartiallyUpdateDevice_NotFound() {
        when(deviceService.partiallyUpdateDevice(anyLong(), anyMap())).thenReturn(null);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "UpdatedDevice");

        ResponseEntity<Device> response = deviceController.partiallyUpdateDevice(1L, updates);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteDevice() {
        when(deviceService.deleteDevice(anyLong())).thenReturn(true);

        ResponseEntity<Void> response = deviceController.deleteDevice(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteDevice_NotFound() {
        when(deviceService.deleteDevice(anyLong())).thenReturn(false);

        ResponseEntity<Void> response = deviceController.deleteDevice(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSearchDevicesByBrand() {
        List<Device> devices = Arrays.asList(new Device("iphone 13", "apple"), new Device("A 15", "apple"));

        when(deviceService.searchDevicesByBrand("apple")).thenReturn(devices);

        ResponseEntity<List<Device>> response = deviceController.searchDevicesByBrand("apple");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
}
