package whu.edu.cs.transitnet.Torch.base;

import whu.edu.cs.transitnet.Torch.queryEngine.model.LightEdge;

import java.util.List;


public interface PathQueryIndex extends Index{

    List<String> findByPath(List<LightEdge> path);
    List<String> findByStrictPath(List<LightEdge> path);
}
