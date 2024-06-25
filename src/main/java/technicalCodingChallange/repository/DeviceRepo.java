package technicalCodingChallange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import technicalCodingChallange.model.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {
    Optional<List<Device>> findByBrand(String brand);
    Optional<Device> findDeviceByName(String name);
}