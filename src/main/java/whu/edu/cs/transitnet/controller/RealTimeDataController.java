package whu.edu.cs.transitnet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import whu.edu.cs.transitnet.service.RealTimeDataService;
import whu.edu.cs.transitnet.vo.RealTimeDataVo;
import whu.edu.cs.transitnet.vo.SpeedDateData;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Controller
public class RealTimeDataController {
    @Resource
    RealTimeDataService realTimeDataService;

    @CrossOrigin
    @GetMapping("/api/realTime")
    @ResponseBody
    public List<RealTimeDataVo> listRealTimeDataLastByRecordedTime(@RequestParam("curTime") String curTime) {
        Timestamp d = Timestamp.valueOf(curTime);
        return realTimeDataService.getRealTimeDataVoLastByRecordedTime(d);
    }

    /**http://localhost:8090/api/mapv/origin
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

    @CrossOrigin
    @GetMapping("/api/realTime/speed")
    @ResponseBody
    public List<SpeedDateData> ListSpeedDateByVehicleLastSevenDay(@RequestParam("vehicleId") String vehicleId, @RequestParam("curTime") String curTime) {
        return realTimeDataService.getSpeedDateListByVehicleIdLastSevenDate(vehicleId, curTime);
    }
}
