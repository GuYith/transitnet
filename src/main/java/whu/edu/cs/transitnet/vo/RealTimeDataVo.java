package whu.edu.cs.transitnet.vo;

public class RealTimeDataVo {
    private Double lat;
    private Double lon;
    private String recordedTime;

    public RealTimeDataVo(Double lat, Double lon, String recordedTime) {
        this.lat = lat;
        this.lon = lon;
        this.recordedTime = recordedTime;
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
