package whu.edu.cs.transitnet.vo;

public class VehicleIdRecordedTimeVo {
    private String vehicleId;
    private String recordTime;

    public VehicleIdRecordedTimeVo(String vehicleId, String recordTime) {
        this.vehicleId = vehicleId;
        this.recordTime = recordTime;
    }
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }
}
