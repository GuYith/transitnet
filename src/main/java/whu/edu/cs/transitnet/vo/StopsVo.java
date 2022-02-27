package whu.edu.cs.transitnet.vo;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.visualization.PointJsonModel;

import java.sql.Time;
import java.util.Date;

public class StopsVo implements TrajEntry {
    private String stopId;
    private String tripId;
    private String stopName;
    private Time arrivalTime;
    private Time departureTime;
    private int stopSequence;
    private Double stopLat;
    private Double stopLon;
    private PointJsonModel pointJsonModel;

    public StopsVo(String stopId, String tripId, String stopName, Date arrivalTime, Date departureTime, int stopSequence, Double stopLat, Double stopLon) {
        this.stopId = stopId;
        this.tripId = tripId;
        this.stopName = stopName;
        this.arrivalTime = Time.valueOf(arrivalTime.toString());
        this.departureTime = Time.valueOf(departureTime.toString());
        this.stopSequence = stopSequence;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.pointJsonModel = new PointJsonModel(stopLon, stopLat);
    }

    public StopsVo(String stopId, String tripId, String stopName, Time arrivalTime, Time departureTime, int stopSequence, Double stopLat, Double stopLon, PointJsonModel pointJsonModel) {
        this.stopId = stopId;
        this.tripId = tripId;
        this.stopName = stopName;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopSequence = stopSequence;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.pointJsonModel = pointJsonModel;
    }
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public double getLat() {
        return this.stopLat;
    }

    @Override
    public double getLng() {
        return this.stopLon;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public Double getStopLat() {
        return stopLat;
    }

    public void setStopLat(Double stopLat) {
        this.stopLat = stopLat;
    }

    public Double getStopLon() {
        return stopLon;
    }

    public void setStopLon(Double stopLon) {
        this.stopLon = stopLon;
    }

    public PointJsonModel getPointJsonModel() {
        return pointJsonModel;
    }

    public void setPointJsonModel(PointJsonModel pointJsonModel) {
        this.pointJsonModel = pointJsonModel;
    }
}
