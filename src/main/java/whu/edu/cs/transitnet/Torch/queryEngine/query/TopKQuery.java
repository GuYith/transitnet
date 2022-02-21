package whu.edu.cs.transitnet.Torch.queryEngine.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whu.edu.cs.transitnet.Torch.base.Index;
import whu.edu.cs.transitnet.Torch.base.TopKQueryIndex;
import whu.edu.cs.transitnet.Torch.base.Torch;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.Mapper;
import whu.edu.cs.transitnet.Torch.queryEngine.model.LightEdge;

import java.util.List;

class TopKQuery extends QueryImpl{

    private static final Logger logger = LoggerFactory.getLogger(TopKQuery.class);
    private TopKQueryIndex index;

    TopKQuery(TopKQueryIndex index, Mapper mapper, TrajectoryResolver resolver){
        super(mapper, resolver);
        this.index = index;
    }

    @Override
    public QueryResult execute(Object K) {

        if (!(K instanceof Integer))
            throw new IllegalStateException(
                    "parameter passed to windowQuery should be of type Integer, " +
                            "which indicates top k results to return");
        if (index.useEdge())
            return topKusingEdge((int)K);
        else
            return topkusingVertex((int)K);
    }

    @Override

    public void updateIdx(Index idx) {
        if (!(idx instanceof TopKQueryIndex))
            throw new IllegalStateException("the index do not support TopK search");

        index = (TopKQueryIndex) idx;
    }

    private QueryResult topKusingEdge(int k) {

        List<String> trajIds = index.findTopK(k, null, LightEdge.copy(mapped.edges), resolver);

        logger.info("total qualified trajectories: {}", trajIds.size());
        logger.info("top {} trajectory id set: {}",trajIds.size(),trajIds);

        return resolver.resolve(Torch.QueryType.TopK, trajIds, raw, mapped);
    }


    private QueryResult topkusingVertex(int k) {

        List<String> trajIds = index.findTopK(k, mapped, null, resolver);
        logger.info("top {} trajectory id set: {}",trajIds.size(),trajIds);
        return resolver.resolve(Torch.QueryType.TopK, trajIds, raw, mapped);
    }
}
