package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.RealTimeDataDao;
import whu.edu.cs.transitnet.pojo.RealTimeDataEntity;
import whu.edu.cs.transitnet.utils.TimeUtil;
import whu.edu.cs.transitnet.vo.RealTimeDataVo;
import whu.edu.cs.transitnet.vo.SpeedDateData;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class RealTimeDataService {
    @Autowired
    RealTimeDataDao realTimeDataDao;

    long TIMESTEP = 1000*60*15;
    long TIMEDATE = 1000*60*60*24;
    long LASTTIMESPAN = 1000*60*5;
    public RealTimeDataEntity getRealTimeDataEntityByVehicleIdAndRecordedTime(String vehicleId, String recordedTime) {
        return realTimeDataDao.findAllByVehicleIdAndRecordedTime(vehicleId, recordedTime);
    }

    public List<RealTimeDataEntity> getRealTimeDataLastByRecordedTime(Timestamp curTime) {
        Timestamp end = curTime;
        Timestamp start = new Timestamp(curTime.getTime() - LASTTIMESPAN);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);
        List<RealTimeDataEntity> realTimeDataEntities = realTimeDataDao.findAllLastByRecordedTimeSpan(startTime, endTime);
        return realTimeDataEntities;
    }
    public List<String> getVehicleIdList(String recordedTime) {
        return realTimeDataDao.findAllVehicleIdByRecordedTime(recordedTime);
    }

    public List<SpeedDateData> getSpeedDateListByVehicleIdLastSevenDate(String vehicleId, String curTime) {
        List<SpeedDateData> speedDateDatas = new ArrayList<>();
        Timestamp lastTime = Timestamp.valueOf(curTime);
        String lastDateStr = curTime.substring(0,10);
        Date lastDate = Date.valueOf(lastDateStr);
        Timestamp startTime = new Timestamp(lastDate.getTime());
        Timestamp endTime = lastTime;
        System.out.println("startTime: " + startTime);
        System.out.println("endTime: " + endTime);
        speedDateDatas.add(getSpeedDateListByVehicleIdAndDate(vehicleId, startTime, lastTime));
        for(Integer i = 0; i < 7; i ++) {
            endTime = startTime;
            startTime = new Timestamp(endTime.getTime() - TIMEDATE);
            System.out.println("startTime: " + startTime);
            System.out.println("endTime: " + endTime);
            SpeedDateData curSpeedData = getSpeedDateListByVehicleIdAndDate(vehicleId, startTime, endTime);
            speedDateDatas.add(curSpeedData);
        }
        return speedDateDatas;
    }

    public SpeedDateData getSpeedDateListByVehicleIdAndDate(String vehicleId, Timestamp start, Timestamp end) {
        List<Double> speedList = new ArrayList<>();
        Timestamp lastTime = start;
        Timestamp loopEndTime = new Timestamp(start.getTime() + TIMEDATE);
        String timeStr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);
        String timeStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);
//        one date
        List<RealTimeDataVo> vehiclePoints =  realTimeDataDao.findAllByVehicleIdByTimeSpan(vehicleId, timeStr1, timeStr2);
        Integer firstIndex = 0;
        Integer tempIdx = 0;
//        while(!lastTime.equals(loopEndTime)) {
//            Timestamp tempTime = new Timestamp(lastTime.getTime() + TIMESTEP);
//            String timeStr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastTime);
//            String timeStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempTime);
//            //one date
//            List<RealTimeDataVo> vehiclePoints =  realTimeDataDao.findAllByVehicleIdByTimeSpan(vehicleId, timeStr1, timeStr2);
//            speedList.add(calSpeed(vehiclePoints));
//            lastTime = tempTime;
//        }
        while(!lastTime.equals(loopEndTime) & tempIdx < vehiclePoints.size()) {
            Timestamp endTime = new Timestamp(lastTime.getTime() + TIMESTEP);
            tempIdx = firstIndex;
            Timestamp tempTime = Timestamp.valueOf(vehiclePoints.get(tempIdx).getRecordedTime());
            //every timespan
            while(tempTime.getTime() < endTime.getTime() & tempIdx < vehiclePoints.size()) {
                tempTime = Timestamp.valueOf(vehiclePoints.get(tempIdx).getRecordedTime());
                tempIdx += 1;
            }
            if(!firstIndex.equals(tempIdx)) {
                List<RealTimeDataVo> PointswithinTimeSpan = vehiclePoints.subList(firstIndex, tempIdx - 1);
                speedList.add(calSpeed(PointswithinTimeSpan));
            }  else {
                speedList.add(0.0);
            }

            firstIndex = tempIdx;
            lastTime = endTime;
        }
//        speedList.size() 24*4
        while(speedList.size() < 24*4) {
            speedList.add(0.0);
        }
        SpeedDateData result = new SpeedDateData(start.toString().substring(5,10), speedList);
        return result;
    }

    private Double calSpeed(List<RealTimeDataVo> realTimeDataVos) {
        TimeUtil timeUtil = new TimeUtil();
        if(realTimeDataVos.size() == 0) return 0.0;
        Double dist = 0.0; //km
        String startTime = realTimeDataVos.get(0).getRecordedTime();
        String endTime = realTimeDataVos.get(realTimeDataVos.size()-1).getRecordedTime();
        for (Integer i = 0; i < realTimeDataVos.size()-1; i ++) {
            RealTimeDataVo rtv1 = realTimeDataVos.get(0);
            RealTimeDataVo rtv2 = realTimeDataVos.get(1);
            dist += timeUtil.getDistance(rtv1.getLon(), rtv1.getLat(), rtv2.getLon(), rtv2.getLat());
        }
        Timestamp time1 =  Timestamp.valueOf(startTime);
        Timestamp time2 =  Timestamp.valueOf(endTime);
        long span = (time2.getTime() - time1.getTime()) / 1000; //  s
        Double speed = dist*3600/span;
        return speed;
    }
}
