package whu.edu.cs.transitnet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import whu.edu.cs.transitnet.pojo.SplitShapeSpeedEntity;
import whu.edu.cs.transitnet.pojo.SplitShapeSpeedEntityPK;

import java.util.List;

public interface SplitShapeSpeedDao extends JpaRepository<SplitShapeSpeedEntity, SplitShapeSpeedEntityPK> {

    List<SplitShapeSpeedEntity> findAll();

    List<SplitShapeSpeedEntity> findAllByShapeId(String shapeId);
}
