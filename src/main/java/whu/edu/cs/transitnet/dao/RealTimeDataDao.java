package whu.edu.cs.transitnet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whu.edu.cs.transitnet.pojo.RealTimeDataEntity;
import whu.edu.cs.transitnet.vo.RealTimePointEntity;

import java.util.List;

public interface RealTimeDataDao extends JpaRepository<RealTimeDataEntity, String> {
    List<RealTimeDataEntity> findAll();

    RealTimeDataEntity findAllByVehicleIdAndRecordedTime(String vehicleId, String recordedTime);

    @Query(value = "SELECT  route_id, direction, trip_id, agency_id, origin_stop, lat, lon, bearing, vehicle_id, aimed_arrival_time, distance_from_origin, presentable_distance, distance_from_next_stop, next_stop, MAX(recorded_time) as recorded_time from real_time_data_temp " +
            "WHERE recorded_time > ?1 AND recorded_time <= ?2 GROUP BY vehicle_id", nativeQuery = true)
    List<RealTimeDataEntity> findAllLastByRecordedTimeSpan(String startTime, String endTime);

//    @Query(value = "SELECT rte from RealTimeDataEntity rte " +
//            "WHERE rte.recordedTime IN (SELECT Max(rte2.recordedTime) FROM RealTimeDataEntity rte2 " +
//            "WHERE rte2.recordedTime > ?1 AND rte2.recordedTime <= ?2 GROUP BY rte2.vehicleId)")
//    List<RealTimeDataEntity> findAllLastByRecordedTimeSpan(String startTime, String endTime);

    @Query(value = "SELECT DISTINCT vehicle_id " +
            "FROM real_time_data_temp " +
            "WHERE recorded_time = ?1", nativeQuery = true)
    List<String> findAllVehicleIdByRecordedTime(String recordedTime);

    @Query(value = "SELECT new whu.edu.cs.transitnet.vo.RealTimePointEntity(rte.vehicleId, rte.lat, rte.lon, rte.recordedTime) " +
            "FROM RealTimeDataEntity rte " +
            "WHERE rte.vehicleId = ?1 AND rte.recordedTime > ?2 AND rte.recordedTime <= ?3 ORDER BY rte.recordedTime")
    List<RealTimePointEntity> findAllVehiclePointsByVehicleIdByTimeSpan(String vehicleId, String startTime, String endTime);

    @Query(value = "SELECT new whu.edu.cs.transitnet.vo.RealTimePointEntity(rte.vehicleId, rte.lat, rte.lon, rte.recordedTime) " +
            "FROM RealTimeDataEntity  rte " +
            "WHERE rte.recordedTime > ?1 AND rte.recordedTime <= ?2 ORDER BY rte.recordedTime")
    List<RealTimePointEntity> findAllVehiclePointsByTimeSpan(String startTime, String endTime);
}
