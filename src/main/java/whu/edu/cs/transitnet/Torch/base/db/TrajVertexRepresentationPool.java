package whu.edu.cs.transitnet.Torch.base.db;

import whu.edu.cs.transitnet.Torch.base.FileSetting;

public class TrajVertexRepresentationPool extends TrajectoryPool {

    public TrajVertexRepresentationPool(boolean isMem, FileSetting setting){
        super(isMem, setting);
        tableName = setting.TRAJECTORY_VERTEX_TABLE;
    }
}
