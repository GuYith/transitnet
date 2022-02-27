package whu.edu.cs.transitnet.Torch.base.model;

/**
 * Classes implemented TrajEntry could be added to Trajectory
 * @see
 */
public interface TrajEntry{
    /**
     * @return id of that trajectory entry
     */
    int getId();

    /**
     * @return double value represents latitude of the entry
     */
    double getLat();

    /**
     * @return double value represents longitude of the entry
     */
    double getLng();
}
