package whu.edu.cs.transitnet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import whu.edu.cs.transitnet.service.ShapeSpeedService;
import whu.edu.cs.transitnet.vo.RouteShapeSpeedVo;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class ShapeSpeedController {
    @Resource
    ShapeSpeedService shapeSpeedService;

    /**
     * Get route shape (traj segment) with speed,
     * @return
     */
    @CrossOrigin
    @GetMapping("/api/routes/speed")
    @ResponseBody
    List<RouteShapeSpeedVo> listAllRouteShapesWithSpeed() {
        return shapeSpeedService.getAllRouteShapeWithSpeed();
    }
}
