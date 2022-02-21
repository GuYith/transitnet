package whu.edu.cs.transitnet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whu.edu.cs.transitnet.pojo.RealTimeDataEntity;

import java.util.List;

public interface RealTimeDataDao extends JpaRepository<RealTimeDataEntity, String> {
    List<RealTimeDataEntity> findAll();

    RealTimeDataEntity findAllByVehicleIdAndRecordedTime(String vehicleId, String recordedTime);

    @Query(value = "SELECT DISTINCT vehicle_id " +
            "FROM real_time_data_temp " +
            "WHERE recorded_time = ?1", nativeQuery = true)
    List<String> findAllVehicleIdByRecordedTime(String recordedTime);
}
