package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.StopsDao;
import whu.edu.cs.transitnet.vo.StopsVo;

import java.util.List;

@Service
public class StopsService {
    @Autowired
    StopsDao stopsDao;

    public List<StopsVo> getStopsByTripId(String tripId) {
        List<StopsVo> stopsVos = stopsDao.findAllByTripId(tripId);
        return stopsVos;
    }
}
