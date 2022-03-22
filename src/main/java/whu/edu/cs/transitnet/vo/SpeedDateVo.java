package whu.edu.cs.transitnet.vo;

import java.util.List;
public class SpeedDateVo {

    private String id;
    private String date;
    private List<Double> speedList;

    public SpeedDateVo(String date, List<Double> speedList) {
        this.date = date;
        this.speedList = speedList;
    }

    public SpeedDateVo(String id, String date, List<Double> speedList) {
        this.id = id;
        this.date = date;
        this.speedList = speedList;
    }

    public SpeedDateVo() {
        this.id = null;
        this.date = null;
        this.speedList = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Double> getSpeedList() {
        return speedList;
    }

    public void setSpeedList(List<Double> speedList) {
        this.speedList = speedList;
    }
}
