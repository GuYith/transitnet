package whu.edu.cs.transitnet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whu.edu.cs.transitnet.pojo.ArrivalTimeEntity;

import java.sql.Time;
import java.util.List;

public interface ArrivalTimeDao extends JpaRepository<ArrivalTimeEntity, Time> {

    @Query(value = "SELECT * FROM  arrival_time " +
            "WHERE time_span >= ?1 AND time_span <= ?2", nativeQuery = true)
    List<ArrivalTimeEntity> findAllByTimeStartAndTimeEnd(String timeStart, String timeEnd);
}
