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
    Integer firstIndex = 0;
    Integer tempIndex = 0;
    public RealTimeDataEntity getRealTimeDataEntityByVehicleIdAndRecordedTime(String vehicleId, String recordedTime) {
        return realTimeDataDao.findAllByVehicleIdAndRecordedTime(vehicleId, recordedTime);
    }

    public List<RealTimeDataEntity> getRealTimeDataLastByRecordedTime(Timestamp curTime) {
        Timestamp end = curTime;
        Timestamp start = new Timestamp(curTime.getTime() - LASTTIMESPAN);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);
        System.out.println(startTime);
        System.out.println(endTime);
        List<RealTimeDataEntity> realTimeDataEntities = realTimeDataDao.findAllLastByRecordedTimeSpan(startTime, endTime);

        for (RealTimeDataEntity re: realTimeDataEntities) {
            System.out.println(re.getVehicleId());
        }
        return realTimeDataEntities;
    }
    public List<String> getVehicleIdList(String recordedTime) {
        return realTimeDataDao.findAllVehicleIdByRecordedTime(recordedTime);
    }

    public List<SpeedDateData> getSpeedDateListByVehicleIdLastSevenDate(String vehicleId, String curTimeStr) {
        List<SpeedDateData> speedDateDatas = new ArrayList<>();
        Timestamp curTime = Timestamp.valueOf(curTimeStr);
        String curTimeDate = curTimeStr.substring(0,10);
        Date lastDate = Date.valueOf(curTimeDate);
        Timestamp startTime = new Timestamp(lastDate.getTime() - 6*TIMEDATE);
        Timestamp endTime = curTime;
        System.out.println("startTime: " + startTime);
        System.out.println("endTime: " + endTime);
        String timeStr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
        String timeStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime);
        List<RealTimeDataVo> vehiclePoints =  realTimeDataDao.findAllByVehicleIdByTimeSpan(vehicleId, timeStr1, timeStr2);
        Timestamp loopEndTime = new Timestamp(lastDate.getTime() + TIMEDATE);

        Integer allPointsCount = vehiclePoints.size();
        firstIndex = 0;
        tempIndex = 0;
        while (!startTime.equals(loopEndTime)) {
            Timestamp dateStart = startTime;
            Timestamp dateEnd = new Timestamp(dateStart.getTime() + TIMEDATE);
            System.out.println("firstIdx: " + firstIndex);
            System.out.println("tempIdx: " + tempIndex);
            SpeedDateData speedDateData = getSpeedDateListByVehicleIdAndDate(dateStart, dateEnd,
                                                            vehiclePoints);
            speedDateDatas.add(speedDateData);
            startTime = dateEnd;
        }
        return speedDateDatas;
    }

    public SpeedDateData getSpeedDateListByVehicleIdAndDate(Timestamp dateStart, Timestamp dateEnd,
                                                            List<RealTimeDataVo> vehiclePoints) {
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
                List<RealTimeDataVo> PointswithinTimeSpan = vehiclePoints.subList(firstIndex, tempIndex - 1);
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

    private Double calSpeed(List<RealTimeDataVo> realTimeDataVos) {
        TimeUtil timeUtil = new TimeUtil();
        if(realTimeDataVos.size() == 0 || realTimeDataVos.size() == 1) return 0.0;
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
