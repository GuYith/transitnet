package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.RealTimeDataDao;
import whu.edu.cs.transitnet.pojo.RealTimeDataEntity;

import java.util.List;

@Service
public class RealTimeDataService {
    @Autowired
    RealTimeDataDao realTimeDataDao;

    public RealTimeDataEntity getRealTimeDataEntityByVehicleIdAndRecordedTime(String vehicleId, String recordedTime) {
        return realTimeDataDao.findAllByVehicleIdAndRecordedTime(vehicleId, recordedTime);
    }

    public List<String> getVehicleIdList(String recordedTime) {
        return realTimeDataDao.findAllVehicleIdByRecordedTime(recordedTime);
    }
}
