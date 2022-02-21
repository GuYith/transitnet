package whu.edu.cs.transitnet.Torch.queryEngine.visualization;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;

import java.util.List;

public class TrajJsonModel {

    Geometry geometry;

    Double size;
    public TrajJsonModel(List<TrajEntry> path){
        geometry = new Geometry("LineString", path);
    }

    public TrajJsonModel(TrajEntry point, Double size) {
        geometry = new Geometry("Point", point);
        this.size = size;
    }
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
