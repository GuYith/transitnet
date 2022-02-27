package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.visualization.TrajJsonModel;
import whu.edu.cs.transitnet.dao.RoutesDao;
import whu.edu.cs.transitnet.dao.ShapesDao;
import whu.edu.cs.transitnet.dao.StopsDao;
import whu.edu.cs.transitnet.dao.TripsDao;
import whu.edu.cs.transitnet.pojo.TripsEntity;
import whu.edu.cs.transitnet.utils.TimeUtil;
import whu.edu.cs.transitnet.vo.RoutesVo;
import whu.edu.cs.transitnet.vo.ShapesVo;
import whu.edu.cs.transitnet.vo.StopsVo;

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
    //TODO modify the code: 1. use the torch model 2. setfunction may be better in the class
    public RoutesVo getRouteVoByRouteIdAndTripId(String routeId, String tripId) {
        List<ShapesVo> shapesVos = shapesDao.findAllByRouteIdAndTripId(routeId, tripId);
        List<TrajEntry> trajEntries = new ArrayList<>();
        for (ShapesVo sv: shapesVos) {
            trajEntries.add(sv);
        }
        RoutesVo routesVo = routesDao.findRoutesVoByRouteId(routeId);
        routesVo.setTrajJsonModel(new TrajJsonModel(trajEntries));
        return routesVo;
    }

    public List<RoutesVo> getRoutesVoOriginList() {
        List<RoutesVo> routesVos = new ArrayList<RoutesVo>();
        List<TripsEntity> tripsEntities = tripsDao.findOriginTrips();
        for (TripsEntity te: tripsEntities) {
            RoutesVo routesVo = getRouteVoByRouteIdAndTripId(te.getRouteId(), te.getTripId());
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
        List<ShapesVo> shapesVos = shapesDao.findAllByRouteIdAndTripId(routeId, tripId);
        List<StopsVo> stopsVos = stopsDao.findAllByTripId(tripId);
        List<Double> timeList = timeUtil.CalculateVehicleStopArriveTimes(stopsVos, shapesVos);
        return timeList;
    }
}
