package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.Torch.queryEngine.visualization.TrajJsonModel;
import whu.edu.cs.transitnet.dao.StopsDao;
import whu.edu.cs.transitnet.vo.StopsVo;

import java.util.Iterator;
import java.util.List;

@Service
public class StopsService {
    @Autowired
    StopsDao stopsDao;

    public List<StopsVo> getStopsByTripId(String tripId) {
        List<StopsVo> stopsVos = stopsDao.findAllByTripId(tripId);
        Iterator<StopsVo> iterator = stopsVos.iterator();
        while(iterator.hasNext()) {
            StopsVo sv = iterator.next();
            sv.setTrajJsonModel(new TrajJsonModel(sv,10.0));
        }
        return stopsVos;
    }
}
