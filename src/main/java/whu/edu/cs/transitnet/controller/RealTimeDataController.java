package whu.edu.cs.transitnet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import whu.edu.cs.transitnet.pojo.RealTimeDataEntity;
import whu.edu.cs.transitnet.service.RealTimeDataService;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class RealTimeDataController {
    @Resource
    RealTimeDataService realTimeDataService;

    /**
     * Get real time data by vehicleId and recordedTime
     * @param vehicleId
     * @param recordedTime
     * @return RealTimeDataEntity
     */
    @CrossOrigin
    @GetMapping("/api/realTime")
    @ResponseBody
    public RealTimeDataEntity listRealTimeDataByVehicleIdAndRecordedTime(@RequestParam("vehicleId") String vehicleId, @RequestParam("recordedTime")String recordedTime) {
        return realTimeDataService.getRealTimeDataEntityByVehicleIdAndRecordedTime(vehicleId, recordedTime);
    }

    /**
     * Get vehicle list by recordedTime
     * @param recordedTime
     * @return List<String>
     */
    @CrossOrigin
    @GetMapping("/api/realTime/vehicle")
    @ResponseBody
    public List<String> ListVehicleIdRecordedTimeVo(@RequestParam("recordedTime") String recordedTime) {
        return realTimeDataService.getVehicleIdList(recordedTime);
    }
}
