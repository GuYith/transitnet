package whu.edu.cs.transitnet.Torch.base.visualization;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.visualization.trajectory.Geometry;

import java.util.List;

public class TrajJsonModel {

    Geometry geometry;

    Double size;
    public TrajJsonModel(List<TrajEntry> path){
        geometry = new Geometry(path);
    }
    public TrajJsonModel(){
    }
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
