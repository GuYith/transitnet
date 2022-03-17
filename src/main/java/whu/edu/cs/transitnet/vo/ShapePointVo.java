package whu.edu.cs.transitnet.vo;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;

public class ShapePointVo implements TrajEntry {
    private Integer id;
    private Double lat;
    private Double lng;

    public ShapePointVo(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public ShapePointVo(Integer id, Double lat, Double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public int getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLng() {
        return lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
