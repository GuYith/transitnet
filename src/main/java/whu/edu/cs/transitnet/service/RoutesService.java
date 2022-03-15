package whu.edu.cs.transitnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cs.transitnet.dao.RoutesDao;
import whu.edu.cs.transitnet.pojo.RoutesEntity;

import java.sql.Date;
import java.util.List;

@Service
public class RoutesService {
    @Autowired
    RoutesDao routesDao;

    public List<RoutesEntity> getAllRoute() {
        return routesDao.findAll();
    }

    public List<RoutesEntity> getAllRoutesByTimeSpan(Date startDate, Date endDate) {
        return routesDao.findRoutesByTimeSpan(startDate, endDate);
    }
}
