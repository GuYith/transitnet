package whu.edu.cs.transitnet.vo;

import java.util.List;

public class SpeedQueryVo {
    private List<String> idList;
    private String dateStr;

    public SpeedQueryVo(List<String> idList, String dateStr) {
        this.idList = idList;
        this.dateStr = dateStr;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
