package whu.edu.cs.transitnet.vo;

import whu.edu.cs.transitnet.pojo.RealTimeDataEntity;

public class RealTimeDataVo {
    private RealTimeDataEntity vehicle;
    private Double speed;

    public RealTimeDataVo(RealTimeDataEntity point, Double speed) {
        this.vehicle = point;
        this.speed = speed;
    }

    public RealTimeDataEntity getPoint() {
        return vehicle;
    }

    public void setPoint(RealTimeDataEntity vehicle) {
        this.vehicle = vehicle;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
