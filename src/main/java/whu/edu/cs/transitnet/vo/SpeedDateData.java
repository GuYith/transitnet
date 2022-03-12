package whu.edu.cs.transitnet.vo;

import java.util.List;
public class SpeedDateData {

    private String date;
    private List<Double> speedList;

    public SpeedDateData(String date, List<Double> speedList) {
        this.date = date;
        this.speedList = speedList;
    }

    public SpeedDateData() {
        this.date = null;
        this.speedList = null;
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
