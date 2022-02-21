package whu.edu.cs.transitnet.Torch.queryEngine.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whu.edu.cs.transitnet.Torch.base.Index;
import whu.edu.cs.transitnet.Torch.base.PathQueryIndex;
import whu.edu.cs.transitnet.Torch.base.Torch;
import whu.edu.cs.transitnet.Torch.base.db.NameEdgeIdLookup;
import whu.edu.cs.transitnet.Torch.base.model.TorEdge;
import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.model.Trajectory;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.Mapper;
import whu.edu.cs.transitnet.Torch.queryEngine.model.LightEdge;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class PathQuery extends QueryImpl {

    private static final Logger logger = LoggerFactory.getLogger(PathQuery.class);
    private PathQueryIndex index;
    private NameEdgeIdLookup lookup;
    private boolean isByStName;

    PathQuery(PathQueryIndex index, Mapper mapper, TrajectoryResolver resolver){
        super(mapper, resolver);
        this.index = index;
        lookup = new NameEdgeIdLookup(resolver.setting);
    }

    @Override
    public QueryResult execute(Object _isStrict) {
        if (mapped == null) throw new IllegalStateException("please invoke prepare(List<T> raw) first");
        if (!(_isStrict instanceof Boolean))
            throw new IllegalStateException("parameter passed to PathQuery should be of type SearchWindow, " +
                "which indicates top k results to return");
        boolean isStrictPath = (Boolean) _isStrict;

        List<LightEdge> queryEdges = LightEdge.copy(mapped.edges);

        List<String> trajIds = isStrictPath ? index.findByStrictPath(queryEdges) : index.findByPath(queryEdges);
        logger.info("trajectory ids found: {}", trajIds);
        return isByStName ?
                resolver.resolve(isStrictPath ? "SPQ" : Torch.QueryType.PathQ, trajIds, null, mapped)
        : resolver.resolve(isStrictPath ? "SPQ" : Torch.QueryType.PathQ, trajIds, raw, mapped);
    }

    @Override
    public void updateIdx(Index idx) {
        if (!(idx instanceof PathQueryIndex))
            throw new IllegalStateException("the index do not support pathQuery");

        index = (PathQueryIndex) idx;
    }

    @Override
    public boolean prepare(List<? extends TrajEntry> raw) {
        this.raw = (List<TrajEntry>)raw;
        Trajectory<TrajEntry> t = new Trajectory<>();
        t.addAll(raw);

        try {
            mapped = (Trajectory<TrajEntry>)(Object)mapper.match(t);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        isByStName = false;
        return true;
    }

    @Override
    public boolean prepare(String streetName){
        int[] ids = lookup.get(streetName);
        if (ids.length == 0) return false;

        mapped = new Trajectory<>();
        List<TorEdge> l = new LinkedList<>();
        for (int id : ids)
            l.add(new TorEdge(id, null,null, 0.));
        logger.debug("edges on street {}: {}", streetName, Arrays.toString(ids));
        mapped.edges = l;
        isByStName = true;
        return true;
    }
}
