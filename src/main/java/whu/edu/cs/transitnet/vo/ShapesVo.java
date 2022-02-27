package whu.edu.cs.transitnet.vo;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
public class ShapesVo implements TrajEntry {
    private double lat;
    private double lng;

    public ShapesVo(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
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

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
