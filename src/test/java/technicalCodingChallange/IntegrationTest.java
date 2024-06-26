package technicalCodingChallange;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import technicalCodingChallange.model.Device;
import technicalCodingChallange.repository.DeviceRepo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DeviceRepo deviceRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/devices";
        deviceRepo.deleteAll();
    }

    @Test
    public void testCreateDevice() {
        Device device = new Device("iphone 11", "apple");

        ResponseEntity<Device> response = restTemplate.postForEntity(baseUrl, device, Device.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(device.getName(), response.getBody().getName());
    }

    @Test
    public void testGetDeviceById() {
        Device savedDevice = deviceRepo.save(new Device("iphone 11", "apple"));

        ResponseEntity<Device> response = restTemplate.getForEntity(baseUrl + "/" + savedDevice.getId(), Device.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedDevice.getName(), response.getBody().getName());
    }

    @Test
    public void testGetAllDevices() throws Exception {
        Device device1 = new Device("iphone 11", "apple");
        Device device2 = new Device("A15", "samsung");
        deviceRepo.saveAll(Arrays.asList(device1, device2));

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "?page=0&size=10&sortBy=id", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode content = root.get("content");
        Device[] devices = objectMapper.treeToValue(content, Device[].class);

        PageImpl<Device> devicePage = new PageImpl<>(Arrays.asList(devices), PageRequest.of(0, 10), root.get("totalElements").asLong());

        assertEquals(2, devicePage.getTotalElements());
        assertEquals(2, devicePage.getContent().size());
    }

    @Test
    public void testUpdateDevice() {
        Device savedDevice = deviceRepo.save(new Device("iphone 11", "apple"));
        Device updatedDevice = new Device("UpdatedDevice", "UpdatedBrand");

        HttpEntity<Device> requestEntity = new HttpEntity<>(updatedDevice);
        ResponseEntity<Device> response = restTemplate.exchange(baseUrl + "/" + savedDevice.getId(), HttpMethod.PUT, requestEntity, Device.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedDevice.getName(), response.getBody().getName());
    }


    @Test
    public void testPartiallyUpdateDevice() {
        Device savedDevice = deviceRepo.save(new Device("iphone 11", "apple"));

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "UpdatedDevice");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updates);
        ResponseEntity<Device> response = restTemplate.exchange(baseUrl + "/" + savedDevice.getId(), HttpMethod.PATCH, requestEntity, Device.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UpdatedDevice", response.getBody().getName());
    }

    @Test
    public void testDeleteDevice() {
        Device savedDevice = deviceRepo.save(new Device("iphone 11", "apple"));

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/" + savedDevice.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Optional<Device> deletedDevice = deviceRepo.findById(savedDevice.getId());
        assertTrue(deletedDevice.isEmpty());
    }

    @Test
    public void testSearchDevicesByBrand() {
        Device device1 = new Device("iphone 11", "apple");
        Device device2 = new Device("iphone 12", "apple");
        deviceRepo.saveAll(Arrays.asList(device1, device2));

        ResponseEntity<Device[]> response = restTemplate.getForEntity(baseUrl + "/search?brand=apple", Device[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }
}
