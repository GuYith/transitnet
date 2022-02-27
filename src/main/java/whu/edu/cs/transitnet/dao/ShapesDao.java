package whu.edu.cs.transitnet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whu.edu.cs.transitnet.pojo.ShapesEntity;
import whu.edu.cs.transitnet.pojo.ShapesEntityPK;
import whu.edu.cs.transitnet.vo.ShapesVo;

import java.util.List;

public interface ShapesDao extends JpaRepository<ShapesEntity, ShapesEntityPK> {

    @Query(value = "SELECT new whu.edu.cs.transitnet.vo.ShapesVo("
            + "se.shapePtLat, se.shapePtLon)"
            + "FROM ShapesEntity se left join TripsEntity te on se.shapeId = te.shapeId "
            + "WHERE te.routeId = ?1 AND te.tripId = ?2 "
            + "ORDER BY se.shapePtSequence")
    List<ShapesVo> findAllByRouteIdAndTripId(String routeId, String tripId);

    ShapesEntity findByShapeIdAndShapePtSequence(String shapeId, String shapePtSequence);
}
