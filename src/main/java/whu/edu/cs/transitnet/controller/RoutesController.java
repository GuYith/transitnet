package whu.edu.cs.transitnet.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import whu.edu.cs.transitnet.pojo.RoutesEntity;
import whu.edu.cs.transitnet.service.RoutesService;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.List;

@Controller
public class RoutesController {
    @Resource
    RoutesService routesService;

    /**
     * Get all routes
     * @return List<RoutesEntity>
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping("/api/routes")
    @ResponseBody
    public List<RoutesEntity> listRoutes() throws Exception{
        return routesService.getAllRoute();
    }

    /**
     * Get routes by timespan
     * @param startDate
     * @param endDate
     * @return List<RoutesEntity>
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping("/api/routes/timespan")
    @ResponseBody
    public List<RoutesEntity> listRoutesByTimeSpan(@Param("startDate") String startDate, @Param("endDate") String endDate) throws Exception {
        Date sD = Date.valueOf(startDate);
        Date eD = Date.valueOf(endDate);
        return routesService.getAllRoutesByTimeSpan(sD, eD);
    }

}
