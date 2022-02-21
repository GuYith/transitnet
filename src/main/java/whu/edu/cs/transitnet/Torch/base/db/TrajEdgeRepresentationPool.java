package whu.edu.cs.transitnet.Torch.base.db;

import whu.edu.cs.transitnet.Torch.base.FileSetting;

public class TrajEdgeRepresentationPool extends TrajectoryPool{

    public TrajEdgeRepresentationPool(boolean isMem, FileSetting setting){
        super(isMem, setting);
        tableName = setting.TRAJECTORY_EDGE_TABLE;
    }
}
