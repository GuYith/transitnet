package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.ArrivalTimeDao;
import whu.edu.cs.transitnet.pojo.ArrivalTimeEntity;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ArrivaltimeService {
    @Autowired
    ArrivalTimeDao arrivaltimeDao;
    public List<ArrivalTimeEntity> getArrivalTimesByDate(Date date) {
        long dateTime = 24*60*60*1000;
        Timestamp start = new Timestamp(date.getTime());
        Timestamp end = new Timestamp(date.getTime() + dateTime);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);
        List<ArrivalTimeEntity> arrivaltimeEntities = arrivaltimeDao.findAllByTimeStartAndTimeEnd(startTime, endTime);
        return arrivaltimeEntities;
    }
}
