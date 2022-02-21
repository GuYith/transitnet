package whu.edu.cs.transitnet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import whu.edu.cs.transitnet.pojo.CalendarEntity;

public interface CalendarDao extends JpaRepository<CalendarEntity, String> {

}
