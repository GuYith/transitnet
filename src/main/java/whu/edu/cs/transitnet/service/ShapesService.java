package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.ShapesDao;
import whu.edu.cs.transitnet.pojo.ShapesEntity;
import whu.edu.cs.transitnet.vo.ShapePointVo;

import java.util.List;

@Service
public class ShapesService {
    @Autowired
    ShapesDao shapesDao;

    public List<ShapePointVo> getShapesByRouteIdAndTripId(String routeId, String tripId) {
        return shapesDao.findAllByRouteIdAndTripId(routeId, tripId);
    }
    public ShapesEntity getByKey(String shapeId, String shapeSequence) {
        return shapesDao.findByShapeIdAndShapePtSequence(shapeId, shapeSequence);
    }

}
