package whu.edu.cs.transitnet.Torch.queryEngine.visualization;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;

public class PointJsonModel {
    Geometry geometry;
    public PointJsonModel(TrajEntry point){
        geometry = new Geometry("Point", point);
    }
}
