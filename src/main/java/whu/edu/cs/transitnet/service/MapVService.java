package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.Torch.base.visualization.TrajJsonModel;
import whu.edu.cs.transitnet.dao.RoutesDao;
import whu.edu.cs.transitnet.dao.ShapesDao;
import whu.edu.cs.transitnet.dao.StopsDao;
import whu.edu.cs.transitnet.dao.TripsDao;
import whu.edu.cs.transitnet.pojo.TripsEntity;
import whu.edu.cs.transitnet.utils.TimeUtil;
import whu.edu.cs.transitnet.vo.RoutesVo;
import whu.edu.cs.transitnet.vo.ShapePointVo;
import whu.edu.cs.transitnet.vo.StopsVo;
import whu.edu.cs.transitnet.vo.StringPair;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class MapVService {
    @Autowired
    ShapesDao shapesDao;
    @Autowired
    RoutesDao routesDao;
    @Autowired
    TripsDao tripsDao;
    @Autowired
    StopsDao stopsDao;
    public RoutesVo getRouteVoByRouteIdAndTripId(String routeId, String tripId) {
        List<ShapePointVo> shapePointVos = shapesDao.findAllByRouteIdAndTripId(routeId, tripId);
        RoutesVo routesVo = routesDao.findRoutesVoByRouteId(routeId);
        routesVo.setTrajJsonModel(new TrajJsonModel(shapePointVos));
        return routesVo;
    }

    public RoutesVo getRouteVoByShapeId(String routeId, String shapeId) {
        List<ShapePointVo> shapePointVos = shapesDao.findAllByShapeId(shapeId);
        RoutesVo routesVo = routesDao.findRoutesVoByRouteId(routeId);
        routesVo.setTrajJsonModel(new TrajJsonModel(shapePointVos));
        return routesVo;
    }

    public List<RoutesVo> getRoutesVoOriginList() {
        List<RoutesVo> routesVos = new ArrayList<RoutesVo>();
        List<StringPair> routeShapeList = tripsDao.findAllRouteIdAndShapeIdPair();
        for(StringPair p: routeShapeList) {
            RoutesVo routesVo = getRouteVoByShapeId(p.getKey(), p.getValue());
            routesVos.add(routesVo);
        }
        return routesVos;
    }

    public List<RoutesVo> getRoutesVoByTimeSpan(Date startDate, Date endDate) {
        List<RoutesVo> routesVos = new ArrayList<RoutesVo>();
        List<TripsEntity> tripsEntities = tripsDao.findAllTripsByTimeSpan(startDate, endDate);
        for (TripsEntity te: tripsEntities) {
            RoutesVo routesVo = getRouteVoByRouteIdAndTripId(te.getRouteId(), te.getTripId());
            routesVos.add(routesVo);
        }
        return routesVos;
    }
    public List<Double> getRouteTimeList(String routeId, String tripId) {
        TimeUtil timeUtil = new TimeUtil();
        List<ShapePointVo> shapePointVos = shapesDao.findAllByRouteIdAndTripId(routeId, tripId);
        List<StopsVo> stopsVos = stopsDao.findAllByTripId(tripId);
        List<Double> timeList = timeUtil.CalculateVehicleStopArriveTimes(stopsVos, shapePointVos);
        return timeList;
    }
}
