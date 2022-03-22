package whu.edu.cs.transitnet.vo;

public class RealTimePointEntity {
    private String vehicleId;
    private Double lat;
    private Double lon;
    private String recordedTime;

    public RealTimePointEntity(String vehicleId, Double lat, Double lon, String recordedTime) {
        this.vehicleId = vehicleId;
        this.lat = lat;
        this.lon = lon;
        this.recordedTime = recordedTime;
    }

    public RealTimePointEntity() {
        this.vehicleId = null;
        this.lat = null;
        this.lon = null;
        this.recordedTime = null;
    }


    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(String recordedTime) {
        this.recordedTime = recordedTime;
    }
}
