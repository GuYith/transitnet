package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.TripsDao;
import whu.edu.cs.transitnet.pojo.TripsEntity;

import java.sql.Date;
import java.util.List;

@Service
public class TripsService {
    @Autowired
    TripsDao tripsDao;

    public List<TripsEntity> getAllTrips() {
        return tripsDao.findAll();
    }

    public List<TripsEntity> getAllRoutesOneTrip() {
        return tripsDao.findOriginTrips();
    }

    public List<TripsEntity> getTripsByRouteId(String routeId) {
        return tripsDao.findAllByRouteId(routeId);
    }

    public List<TripsEntity> getTripsByRouteIdAndTimeSpan(String routeId, Date startDate, Date endDate) {
        return tripsDao.findAllTripsByRouteIdAndTimeSpan(routeId, startDate, endDate);
    }
}
