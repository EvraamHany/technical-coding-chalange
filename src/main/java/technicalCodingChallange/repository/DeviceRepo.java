package technicalCodingChallange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import technicalCodingChallange.model.Device;

import java.util.List;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long> {
    List<Device> findByBrand(String brand);
}