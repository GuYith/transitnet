package whu.edu.cs.transitnet.pojo;

public class IndexEntity {
    Integer firstIndex;
    Integer tempIndex;

    public IndexEntity(Integer firstIndex, Integer tempIndex) {
        this.firstIndex = firstIndex;
        this.tempIndex = tempIndex;
    }

    public Integer getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(Integer firstIndex) {
        this.firstIndex = firstIndex;
    }

    public Integer getTempIndex() {
        return tempIndex;
    }

    public void setTempIndex(Integer tempIndex) {
        this.tempIndex = tempIndex;
    }
}
