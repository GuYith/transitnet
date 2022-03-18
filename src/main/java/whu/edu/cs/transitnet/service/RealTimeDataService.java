package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.RealTimeDataDao;
import whu.edu.cs.transitnet.pojo.RealTimeDataEntity;
import whu.edu.cs.transitnet.utils.TimeUtil;
import whu.edu.cs.transitnet.vo.RealTimeDataVo;
import whu.edu.cs.transitnet.vo.RealTimePointEntity;
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
    Integer firstIndex = 0;
    Integer tempIndex = 0;
    public RealTimeDataEntity getRealTimeDataEntityByVehicleIdAndRecordedTime(String vehicleId, String recordedTime) {
        return realTimeDataDao.findAllByVehicleIdAndRecordedTime(vehicleId, recordedTime);
    }

    public List<RealTimeDataVo> getRealTimeDataVoLastByRecordedTime(Timestamp curTime) {
        Timestamp end = curTime;
        Timestamp start = new Timestamp(curTime.getTime() - LASTTIMESPAN);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);
        List<RealTimeDataEntity> realTimeDataEntities = realTimeDataDao.findAllLastByRecordedTimeSpan(startTime, endTime);
        List<RealTimePointEntity> realTimePointEntities = realTimeDataDao.findAllVehiclePointsByTimeSpan(startTime, endTime);
        List<RealTimeDataVo> realTimeDataVos = new ArrayList<>();
        for (RealTimeDataEntity re: realTimeDataEntities) {
            String curVehicleId = re.getVehicleId();
            List<RealTimePointEntity> tempList = new ArrayList<>();
            for (RealTimePointEntity rtp: realTimePointEntities) {
                if(rtp.getVehicleId().equals(curVehicleId)) tempList.add(rtp);
            }
            Double speed = calSpeed(tempList);
            realTimeDataVos.add(new RealTimeDataVo(re, speed));
        }
        return realTimeDataVos;
    }
    public List<String> getVehicleIdList(String recordedTime) {
        return realTimeDataDao.findAllVehicleIdByRecordedTime(recordedTime);
    }

    public List<SpeedDateData> getSpeedDateListByVehicleIdLastSevenDate(String vehicleId, String curTimeStr) {
        List<SpeedDateData> speedDateDatas = new ArrayList<>();
        Timestamp curTime = Timestamp.valueOf(curTimeStr);
        String curTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(curTime);
        curTimeDate = curTimeDate.substring(0,10);
        Date lastDate = Date.valueOf(curTimeDate);
        Timestamp startTime = new Timestamp(lastDate.getTime() - 6*TIMEDATE);
        Timestamp endTime = curTime;
        System.out.println("startTime: " + startTime);
        System.out.println("endTime: " + endTime);
        String timeStr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
        String timeStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime);
        List<RealTimePointEntity> vehiclePoints =  realTimeDataDao.findAllVehiclePointsByVehicleIdByTimeSpan(vehicleId, timeStr1, timeStr2);
        Timestamp loopEndTime = new Timestamp(lastDate.getTime() + TIMEDATE);
        Integer allPointsCount = vehiclePoints.size();
        firstIndex = 0;
        tempIndex = 0;
        while (!startTime.equals(loopEndTime)) {
            Timestamp dateStart = startTime;
            Timestamp dateEnd = new Timestamp(dateStart.getTime() + TIMEDATE);
            SpeedDateData speedDateData = getSpeedDateListByVehicleIdAndDate(dateStart, dateEnd,
                                                            vehiclePoints);
            speedDateDatas.add(speedDateData);
            startTime = dateEnd;
        }
        return speedDateDatas;
    }

    public SpeedDateData getSpeedDateListByVehicleIdAndDate(Timestamp dateStart, Timestamp dateEnd,
                                                            List<RealTimePointEntity> vehiclePoints) {
        Timestamp lastTime = dateStart;
        List<Double> speedList = new ArrayList<>();
        Integer allPointsCount = vehiclePoints.size();
        while(!lastTime.equals(dateEnd) & tempIndex < allPointsCount) {
            Timestamp timeSpanEnd = new Timestamp(lastTime.getTime() + TIMESTEP);
            tempIndex = firstIndex;
            Timestamp tempTime = Timestamp.valueOf(vehiclePoints.get(tempIndex).getRecordedTime());
            //every timespan
            while(tempTime.getTime() < timeSpanEnd.getTime() & tempIndex < allPointsCount) {
                tempTime = Timestamp.valueOf(vehiclePoints.get(tempIndex).getRecordedTime());
                tempIndex += 1;
            }
            if(!firstIndex.equals(tempIndex)) {
                List<RealTimePointEntity> PointswithinTimeSpan = vehiclePoints.subList(firstIndex, tempIndex - 1);
                speedList.add(calSpeed(PointswithinTimeSpan));
            }  else {
                speedList.add(0.0);
            }
            if (firstIndex.equals(tempIndex))
                firstIndex = tempIndex;
            else
                firstIndex = tempIndex-1;
            lastTime = timeSpanEnd;
        }
//        speedList.size() 24*4
        while(speedList.size() < 24*4) {
            speedList.add(0.0);
        }
        SpeedDateData result = new SpeedDateData(dateStart.toString().substring(5,10), speedList);
        return result;
    }

    private Double calSpeed(List<RealTimePointEntity> realTimePointEntities) {
        TimeUtil timeUtil = new TimeUtil();
        if(realTimePointEntities.size() == 0 || realTimePointEntities.size() == 1) return 0.0;
        Double dist = 0.0; //km
        String startTime = realTimePointEntities.get(0).getRecordedTime();
        String endTime = realTimePointEntities.get(realTimePointEntities.size()-1).getRecordedTime();
        for (Integer i = 0; i < realTimePointEntities.size()-1; i ++) {
            RealTimePointEntity rtv1 = realTimePointEntities.get(0);
            RealTimePointEntity rtv2 = realTimePointEntities.get(1);
            dist += timeUtil.getDistance(rtv1.getLon(), rtv1.getLat(), rtv2.getLon(), rtv2.getLat());
        }
        Timestamp time1 =  Timestamp.valueOf(startTime);
        Timestamp time2 =  Timestamp.valueOf(endTime);
        long span = (time2.getTime() - time1.getTime()) / 1000; //  s
        if(span == 0) return 0.0;
        else return dist*3600/span;
    }
}
