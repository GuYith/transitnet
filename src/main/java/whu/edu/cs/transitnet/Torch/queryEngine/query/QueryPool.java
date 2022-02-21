package whu.edu.cs.transitnet.Torch.queryEngine.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whu.edu.cs.transitnet.Torch.base.FileSetting;
import whu.edu.cs.transitnet.Torch.base.Index;
import whu.edu.cs.transitnet.Torch.base.Torch;
import whu.edu.cs.transitnet.Torch.base.db.TrajVertexRepresentationPool;
import whu.edu.cs.transitnet.Torch.base.invertedIndex.EdgeInvertedIndex;
import whu.edu.cs.transitnet.Torch.base.invertedIndex.VertexInvertedIndex;
import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.model.Trajectory;
import whu.edu.cs.transitnet.Torch.base.spatialIndex.LEVI;
import whu.edu.cs.transitnet.Torch.base.spatialIndex.VertexGridIndex;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.Mapper;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.Mappers;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.TorGraph;
import whu.edu.cs.transitnet.Torch.mapMatching.model.TowerVertex;
import whu.edu.cs.transitnet.Torch.queryEngine.model.TimeInterval;
import whu.edu.cs.transitnet.Torch.queryEngine.similarity.SimilarityFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryPool extends HashMap<String, Query> {
//访问池
    private static final Logger logger = LoggerFactory.getLogger(QueryPool.class);
    private final QueryProperties props;
    private Mapper mapper;
    private EdgeInvertedIndex edgeInvertedIndex;
    private whu.edu.cs.transitnet.Torch.base.spatialIndex.LEVI LEVI;
    private static AtomicInteger gNameGen = new AtomicInteger();
    private String graphId;

    private Map<Integer, TowerVertex> idVertexLookup;
    private FileSetting setting;
    private TrajectoryResolver resolver;

    /**
     * initilize supported indexes for the 4 types of queries.
     * @param props
     */
    public QueryPool(QueryProperties props) {
        //set client preference
        this.props = props;
        setting = new FileSetting(props.baseDir);
        setting.update(props.uriPrefix);
        edgeInvertedIndex = new EdgeInvertedIndex(setting);
        //initialize queries and map-matching algorithm
        init();

    }

    private void init() {

        buildMapper();
        resolver = new TrajectoryResolver(props.resolveAll, props.isNantong, setting);

        if (props.queryUsed.contains(Torch.QueryType.PathQ))
            put(Torch.QueryType.PathQ, initPathQuery());

        if (props.queryUsed.contains(Torch.QueryType.RangeQ))
            put(Torch.QueryType.RangeQ, initRangeQuery());

        if (props.queryUsed.contains(Torch.QueryType.TopK))
            put(Torch.QueryType.TopK, initTopKQuery());
    }


    private void buildMapper() {
        if (mapper != null)
            return;

        String vehicleType = null;
        String osmPath = null;

        //read meta properties
        try (FileReader fr = new FileReader(setting.metaURI);
             BufferedReader reader = new BufferedReader(fr)) {
            vehicleType = reader.readLine();
            osmPath = reader.readLine();

        } catch (IOException e) {
            logger.error("some critical data is missing, system on exit...");
            System.exit(-1);
        }

        this.graphId = String.valueOf(gNameGen.intValue());
        gNameGen.getAndIncrement();
        TorGraph graph = TorGraph.newInstance(String.valueOf(graphId), setting).
                initGH(setting.hopperURI, osmPath, vehicleType).buildFromDiskData();
        idVertexLookup = graph.idVertexLookup;
        mapper = Mappers.getMapper(Torch.Algorithms.HMM, graph);


    }

    private Query initPathQuery() {

        initEdgeInvertedIndex();

        return new PathQuery(edgeInvertedIndex, mapper, resolver);
    }

    private Query initTopKQuery() {

        // edge based top K
        if (props.preferedIndex.equals(Torch.Index.EDGE_INVERTED_INDEX)) {
            initEdgeInvertedIndex();
            return props.resolveAll ? new TopKQuery(edgeInvertedIndex, mapper, resolver) :
                    new TopKQuery(edgeInvertedIndex, mapper, resolver);
        }

        // point based topK with GVI
        initLEVI();
        return new TopKQuery(LEVI, mapper, resolver);
    }

    private Query initRangeQuery() {
        initEdgeInvertedIndex();
        if (LEVI == null) initLEVI();
        return new WindowQuery(LEVI, resolver);
    }

    private void initEdgeInvertedIndex() {
        if (!edgeInvertedIndex.loaded) {
            if (!edgeInvertedIndex.build(setting.EDGE_INVERTED_INDEX))
                throw new RuntimeException("some critical data is missing, system on exit...");
            edgeInvertedIndex.loaded = true;
        }
    }


    private void initLEVI() {

        if (LEVI!=null) return;

        VertexInvertedIndex vertexInvertedIndex = new VertexInvertedIndex(setting);
        VertexGridIndex vertexGridIndex = new VertexGridIndex(idVertexLookup, 100);
        TrajVertexRepresentationPool trajVertexRepresentationPool = new TrajVertexRepresentationPool(false, setting);

        if (!vertexInvertedIndex.build(setting.VERTEX_INVERTED_INDEX))
            throw new RuntimeException("some critical data is missing, system on exit...");
        vertexInvertedIndex.loaded = true;


        if (!vertexGridIndex.loaded){
            if (!vertexGridIndex.build(setting.GRID_INDEX))
                throw new RuntimeException("some critical data is missing, system on exit...");
            vertexGridIndex.loaded = true;
        }

        SimilarityFunction.MeasureType measureType = convertMeasureType(props.similarityMeasure);
        this.LEVI = new LEVI(vertexInvertedIndex, vertexGridIndex, measureType, trajVertexRepresentationPool, idVertexLookup, setting);
    }

    public void update(String queryType, Map<String,String> props) {
        Query q = get(queryType);

        if (props.containsKey("simFunc") && LEVI != null) {

            String simFunc = props.get("simFunc");
            if (simFunc.equals("LORS")){
                q.updateIdx(convertIndex(Torch.Index.EDGE_INVERTED_INDEX));
            }else{
                q.updateIdx(convertIndex(Torch.Index.LEVI));
                LEVI.updateMeasureType(convertMeasureType(props.get("simFunc")));
            }
        }

        if (props.containsKey("epsilon")&& LEVI != null)
            LEVI.updateEpsilon(Integer.valueOf(props.get("epsilon")));
    }

    public void setTimeInterval(TimeInterval span, boolean contain){
        resolver.setTimeInterval(span, contain);
    }

    private Index convertIndex(String indexType){
        Index index;
        switch (indexType){
            case Torch.Index.EDGE_INVERTED_INDEX:
                if (!edgeInvertedIndex.loaded)
                    initEdgeInvertedIndex();
                index = edgeInvertedIndex;
                break;
            case Torch.Index.LEVI:
                if (LEVI == null)
                    initLEVI();
                index = LEVI;
                break;
            default:
                throw new IllegalStateException("please lookup Torch.Index for currently supported index type");
        }
        return index;
    }

    private SimilarityFunction.MeasureType convertMeasureType(String preferedDistFunc) {
        SimilarityFunction.MeasureType measureType;
        switch (preferedDistFunc) {
            case Torch.Algorithms.DTW:
                measureType = SimilarityFunction.MeasureType.DTW;
                break;
            case Torch.Algorithms.Frechet:
                measureType = SimilarityFunction.MeasureType.Frechet;
                break;
            case Torch.Algorithms.Hausdorff:
                measureType = SimilarityFunction.MeasureType.Hausdorff;
                break;
            case Torch.Algorithms.LCSS:
                measureType = SimilarityFunction.MeasureType.LCSS;
                break;
            case Torch.Algorithms.EDR:
                measureType = SimilarityFunction.MeasureType.EDR;
                break;
            default:
                throw new IllegalStateException("please lookup Torch.Algorithms for currently supported measure type");
        }
        return measureType;
    }

    public QueryResult resolve(int[] idArr) {
        List<Trajectory<TrajEntry>> resolved = resolver.resolveResult(idArr);
        return new QueryResult(resolved);
    }

    public FileSetting getFileSettings() {
        return setting;
    }
}
