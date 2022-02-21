package whu.edu.cs.transitnet.vo;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;

public class ShapesVo implements TrajEntry {
    private String shapeId;
    private double lat;
    private double lng;
    private Integer shapePtSequence;
    private String routeId;

    public ShapesVo(String shapeId, double lat, double lng, Integer shapePtSequence, String routeId) {
        this.shapeId = shapeId;
        this.lat = lat;
        this.lng = lng;
        this.shapePtSequence = shapePtSequence;
        this.routeId = routeId;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLng() {
        return lng;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Integer getShapePtSequence() {
        return shapePtSequence;
    }

    public void setShapePtSequence(Integer shapePtSequence) {
        this.shapePtSequence = shapePtSequence;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
