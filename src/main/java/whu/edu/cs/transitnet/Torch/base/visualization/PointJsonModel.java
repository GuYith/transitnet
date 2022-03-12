package whu.edu.cs.transitnet.Torch.base.visualization;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.visualization.point.Geometry;

public class PointJsonModel {
    Geometry geometry;
    public PointJsonModel(TrajEntry point){
        geometry = new Geometry(point);
    }

    public PointJsonModel(Double lon, Double lat) {
        geometry = new Geometry(lon, lat);
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
