package whu.edu.cs.transitnet.Torch.base;

import whu.edu.cs.transitnet.Torch.queryEngine.model.Geometry;

import java.util.List;

public interface WindowQueryIndex extends Index{
    List<String> findInRange(Geometry geometry);
}
