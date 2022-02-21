package whu.edu.cs.transitnet;

import org.springframework.stereotype.Component;
import whu.edu.cs.transitnet.vo.ShapesVo;
import whu.edu.cs.transitnet.vo.StopsVo;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimeUtil {

    private static double EARTH_RADIUS = 6378.137;
    public static TimeUtil timeUtil;

    public List<Double> findNearestPoint(List<StopsVo> stopsVos, List<ShapesVo> shapesVos) {
        System.out.println("Calculating Route Time...");
        List<Integer> nearestIdxs = new ArrayList<Integer>();
        StopsVo lastStop = null;
        Integer cnt = 0;
        Double speed = 0.0;
        List<Double> speeds = new ArrayList<Double>();
        for (StopsVo stop: stopsVos) {
            double minDistance = Double.MAX_VALUE;
            Integer idx = 0;
            for(Integer i = 0; i < shapesVos.size(); i ++) {
                ShapesVo shape = shapesVos.get(i);
                double distance = getDistance(stop.getLng(), stop.getLat(), shape.getLng(), shape.getLat());
                if(distance < minDistance) {
                    idx = i;
                    minDistance = distance;
                }
            }
            nearestIdxs.add(idx);
            if(cnt > 0) {
                speed = getDistance(lastStop.getLng(), lastStop.getLat(), stop.getLng(), stop.getLat());
                speeds.add(speed);
            }
            cnt ++;
            lastStop = stop;
            System.out.println(idx);
        }
        Integer stopIdx = 0;
        ShapesVo stopNearest = shapesVos.get(nearestIdxs.get(stopIdx));
        ShapesVo lastShape = null;
        List<Double> times = new ArrayList<Double>();
        for(Integer i = 0; i < shapesVos.size(); i ++) {
            ShapesVo shapesVo = shapesVos.get(i);
            if(i > 0) {
                double s = getDistance(shapesVo.getLng(), shapesVo.getLat(), lastShape.getLng(), lastShape.getLat());
                double t = s/speeds.get(stopIdx);
                times.add(t*500);
            }
            lastShape = shapesVo;
            if(i != 0 && stopNearest.getLat() == shapesVo.getLat() && stopNearest.getLng() == shapesVo.getLng()) {
                System.out.println(i);
                stopIdx ++;
                stopNearest = shapesVos.get(nearestIdxs.get(stopIdx));
            }
        }
        System.out.println("Finish the Calculation");
        return times;
    }

//    public static void main(String[] args) {
//        timeUtil.findNearestPoint("S46", "CA_D1-Saturday-000000_S4696_40");
//    }
    public double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double lng_1 = Math.toRadians(lng1);
        double lat_1 = Math.toRadians(lat1);
        double lng_2 = Math.toRadians(lng2);
        double lat_2 = Math.toRadians(lat2);
        double a = lat_1 - lat_2;
        double b = lng_1 - lng_2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2) +
                Math.cos(lat_1) * Math.cos(lat_2) * Math.pow(Math.sin(b/2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }
}
