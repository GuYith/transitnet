package whu.edu.cs.transitnet.pojo;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "arrival_time", schema = "gtfs_data")
public class ArrivalTimeEntity {
    private Timestamp timeSpan;
    private String allCount;
    private String lateCount;

    @Id
    @Column(name = "time_span")
    public Timestamp getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(Timestamp timeSpan) {
        this.timeSpan = timeSpan;
    }

    @Basic
    @Column(name = "all_count")
    public String getAllCount() {
        return allCount;
    }

    public void setAllCount(String allCount) {
        this.allCount = allCount;
    }

    @Basic
    @Column(name = "late_count")
    public String getLateCount() {
        return lateCount;
    }

    public void setLateCount(String lateCount) {
        this.lateCount = lateCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrivalTimeEntity that = (ArrivalTimeEntity) o;
        return Objects.equals(timeSpan, that.timeSpan) &&
                Objects.equals(allCount, that.allCount) &&
                Objects.equals(lateCount, that.lateCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeSpan, allCount, lateCount);
    }
}
