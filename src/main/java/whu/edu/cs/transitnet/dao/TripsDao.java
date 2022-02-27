package whu.edu.cs.transitnet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whu.edu.cs.transitnet.pojo.TripsEntity;

import java.sql.Date;
import java.util.List;

public interface TripsDao extends JpaRepository<TripsEntity, String> {
    List<TripsEntity> findAll();
    List<TripsEntity> findAllByRouteId(String routeId);

    @Query(value = "SELECT * FROM trips GROUP BY route_id", nativeQuery = true)
    List<TripsEntity> findOriginTrips();

    @Query(value = "SELECT * FROM trips WHERE service_id IN (SELECT service_id "
            + "FROM calendar WHERE start_date <= ?1 and end_date >= ?2)  "
            + " GROUP BY route_id",
            nativeQuery = true)
    List<TripsEntity> findAllTripsByTimeSpan(Date startDate, Date endDate);

    @Query(value = "SELECT * FROM trips WHERE service_id IN (SELECT service_id "
            + "FROM calendar WHERE start_date <= ?2 and end_date >= ?3) "
            + " AND route_id = ?1", nativeQuery = true)
    List<TripsEntity> findAllTripsByRouteIdAndTimeSpan(String routeId, Date startDate, Date endDate);
}
