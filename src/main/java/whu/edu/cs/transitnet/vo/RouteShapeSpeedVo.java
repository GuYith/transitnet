package whu.edu.cs.transitnet.vo;

import whu.edu.cs.transitnet.Torch.base.visualization.TrajJsonModel;

import java.util.List;

public class RouteShapeSpeedVo {
    String routeId;
    String shapeId;
    List<TrajJsonModel> trajJsonModels;
    List<Double> speeds;

    public RouteShapeSpeedVo(String routeId, String shapeId, List<TrajJsonModel> trajJsonModels, List<Double> speeds) {
        this.routeId = routeId;
        this.shapeId = shapeId;
        this.trajJsonModels = trajJsonModels;
        this.speeds = speeds;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public List<TrajJsonModel> getTrajJsonModels() {
        return trajJsonModels;
    }

    public void setTrajJsonModels(List<TrajJsonModel> trajJsonModels) {
        this.trajJsonModels = trajJsonModels;
    }

    public List<Double> getSpeeds() {
        return speeds;
    }

    public void setSpeeds(List<Double> speeds) {
        this.speeds = speeds;
    }
}
