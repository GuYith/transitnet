package whu.edu.cs.transitnet.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import whu.edu.cs.transitnet.pojo.TripsEntity;
import whu.edu.cs.transitnet.service.TripsService;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.List;

@Controller
public class TripsController {
    @Resource
    TripsService tripsService;

    /**
     * Get trips by routeId
     * @param routeId
     * @return List<TripsEntity>
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping("/api/trips")
    @ResponseBody
    public List<TripsEntity> listTripsByRouteId(@Param("routeId") String routeId) throws Exception{
        return tripsService.getTripsByRouteId(routeId);
    }

    /**
     * Get trips by routeId and timespan
     * @param routeId
     * @param startDate
     * @param endDate
     * @return List<TripsEntity>
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping("/api/trips/timespan")
    @ResponseBody
    public List<TripsEntity> listTripsByRouteIdAndTimeSpan(@Param("routeId") String routeId, @Param("startDate") String startDate, @Param("endDate") String endDate) throws Exception{
        Date sD = Date.valueOf(startDate);
        Date eD = Date.valueOf(endDate);
        return tripsService.getTripsByRouteIdAndTimeSpan(routeId, sD, eD);
    }
}
