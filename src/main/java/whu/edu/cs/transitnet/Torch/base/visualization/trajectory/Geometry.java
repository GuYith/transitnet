package whu.edu.cs.transitnet.Torch.base.visualization.trajectory;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;

import java.util.List;
public class Geometry {

    String type = "LineString";

    //lng, lat
    //order is vital
    double[][] coordinates;

    public Geometry(List<TrajEntry> path){
        int pathLen = path == null ? 0 : path.size();
        coordinates = new double[pathLen][2];
        for (int i = 0; i < pathLen; i++){
            coordinates[i][0] = path.get(i).getLng();
            coordinates[i][1] = path.get(i).getLat();
        }
    }


    public double[][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][] coordinates) {
        this.coordinates = coordinates;
    }
}