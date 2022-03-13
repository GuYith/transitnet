package whu.edu.cs.transitnet.Torch.base.visualization.point;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;

public class Geometry {
    String type = "Point";
    double[] coordinates;

    public Geometry(TrajEntry point) {
        coordinates = new double[2];
        coordinates[0]= point.getLng();
        coordinates[1] = point.getLat();
    }

    public Geometry(Double lon, Double lng) {
        coordinates = new double[2];
        coordinates[0]= lon;
        coordinates[1] = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
