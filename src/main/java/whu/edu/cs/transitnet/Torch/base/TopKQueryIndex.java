package whu.edu.cs.transitnet.Torch.base;

import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.queryEngine.model.LightEdge;
import whu.edu.cs.transitnet.Torch.queryEngine.query.TrajectoryResolver;

import java.util.List;

public interface TopKQueryIndex extends Index{
    <T extends TrajEntry> List<String> findTopK(int k, List<T> pointQuery, List<LightEdge> edgeQuery, TrajectoryResolver resolver);
    boolean useEdge();
}
