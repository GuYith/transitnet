package whu.edu.cs.transitnet.Torch.queryEngine.visualization;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;

import java.util.List;
//TODO Gemetry Modified
public class Geometry {

    String type;

    //lng, lat
    //order is vital
    double[][] coordinates;

    public Geometry(String type, List<TrajEntry> path){
        this.type = type;
        int pathLen = path == null ? 0 : path.size();
        coordinates = new double[pathLen][2];
        for (int i = 0; i < pathLen; i++){
            coordinates[i][0] = path.get(i).getLng();
            coordinates[i][1] = path.get(i).getLat();
        }
    }

    public Geometry(String type, TrajEntry point) {
        this.type = type;
        coordinates = new double[1][2];
        coordinates[0][0] = point.getLng();
        coordinates[0][1] = point.getLat();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][] coordinates) {
        this.coordinates = coordinates;
    }
}
