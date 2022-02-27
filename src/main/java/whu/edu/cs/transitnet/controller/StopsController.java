package whu.edu.cs.transitnet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import whu.edu.cs.transitnet.service.StopsService;
import whu.edu.cs.transitnet.vo.StopsVo;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class StopsController {
    @Resource
    StopsService stopsService;

    /**
     * Get stops by tripId
     * @param tripId
     * @return List<StopsVo>
     */
    @CrossOrigin
    @GetMapping("/api/stops")
    @ResponseBody
    public List<StopsVo> listStops(@RequestParam(value = "tripId") String tripId) {
        return stopsService.getStopsByTripId(tripId);
    }
}
