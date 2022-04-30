package whu.edu.cs.transitnet.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import whu.edu.cs.transitnet.pojo.ArrivalTimeEntity;
import whu.edu.cs.transitnet.service.ArrivaltimeService;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.List;

@Controller
public class ArrivalTimeController {
    @Resource
    ArrivaltimeService arrivaltimeService;

    /**
     * get the bus arrival time info on date, include all stop count, late count and timespan
     * @param date
     * @return
     */
    @CrossOrigin
    @GetMapping("/api/visual/arrivalTime")
    @ResponseBody
    List<ArrivalTimeEntity> listArrivalTimes(@Param("date") String date) {
        Date d = Date.valueOf(date);
        List<ArrivalTimeEntity> arrivaltimeEntities = arrivaltimeService.getArrivalTimesByDate(d);
        return arrivaltimeEntities;
    }
}
