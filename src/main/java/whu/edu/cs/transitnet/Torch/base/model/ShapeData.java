package whu.edu.cs.transitnet.Torch.base.model;

public class ShapeData implements TrajEntry {
    private int shapeId;
    private final double lat;
    private final double lng;
    private final String rawId;

    public ShapeData(String strline) {
        String[] temp = strline.split(",");
        this.rawId = temp[0];
        this.lat = Double.parseDouble(temp[1]);
        this.lng = Double.parseDouble(temp[2]);
    }
    public void printShapeRawData() {
        System.out.println("{ "+ this.lat + ", " + this.lng + "}");
    }
    public void setId(int id) {
        this.shapeId = id;
    }

    @Override
    public int getId() {
        return this.shapeId;
    }

    @Override
    public double getLat() {
        return this.lat;
    }

    @Override
    public double getLng() {
        return this.lng;
    }
    public String getRawId() {
        return this.rawId;
    }
}
