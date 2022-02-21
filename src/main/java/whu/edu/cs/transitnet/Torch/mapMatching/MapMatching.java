package whu.edu.cs.transitnet.Torch.mapMatching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whu.edu.cs.transitnet.Torch.base.FileSetting;
import whu.edu.cs.transitnet.Torch.base.Torch;
import whu.edu.cs.transitnet.Torch.base.helper.MemoryUsage;
import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.model.Trajectory;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.Mapper;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.Mappers;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.TorDijkstra;
import whu.edu.cs.transitnet.Torch.mapMatching.algorithm.TorGraph;
import whu.edu.cs.transitnet.Torch.mapMatching.model.TowerVertex;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * An mapMatching object is for projecting raw trajectory data to graph.
 * There are options to customize your mapMatching object.
 * @see Builder
 *
 * Note that there will be only one MapMatching object for each application instance.
 * Do not share it between threads as it is not designed to do so.
 *
 * Usage:
 * MapMatching mm = MapMatching.getBuilder().build("Resources/porto_raw_trajectory.txt","Resources/porto.osm.pbf");
 * mm.start();
 * @see #start()
 */
public class MapMatching {
    
    public static final String GRAPHNAME = "g";
    private static Logger logger = LoggerFactory.getLogger(MapMatching.class);
    private static Builder builder = new Builder();
    private MMProperties props;
    private TorGraph graph;
    private Mapper mapper;
    public static Builder getBuilder(){
        return builder;
    }
    private FileSetting setting;

    private MapMatching(MMProperties props){
        
        this.props = props;

        //check trajSrcPath file
        File trajFile = new File(props.trajSrcPath);
        if (!trajFile.exists()) {
            logger.error("{} does not exist", props.trajSrcPath);
            throw new RuntimeException();
        }

        File PBFFile = new File(props.osmPath);
        if (!PBFFile.exists()){
            logger.error("{} does not exist", props.osmPath);
            System.exit(-1);
            throw new RuntimeException();
        }

        String baseDir = props.baseDir;
        setting = new FileSetting(baseDir);
        
        //check output directory
        File dir = new File(setting.TorchBase);
        if (!dir.exists()) {
            if (!dir.mkdirs()){
                logger.error("{} cannot make directory, possibly Torch do not have permission for it.", setting.TorchBase);
                throw new RuntimeException();
            }
        }else if (!dir.isDirectory()){
            logger.error("{} already exists and it is not a directory", setting.TorchBase);
            throw new RuntimeException();
        }
    }

    /**
     * readBatch raw trajectory data --> map it on graph --> store mapped trajectories on disk.
     * Since some times the trajectory data file is too large and it cannot be loaded into memory at once,
     * the subroutine will readBatch and do the work on batch. The batch size could be specified via
     *
     * @see Builder#setBatchSize(int)
     */
    public void start(){

        MemoryUsage.start();

        //readBatch and build graph
        if (graph == null) {
            graph = TorGraph.newInstance(GRAPHNAME, setting).
                    initGH(setting.hopperURI, props.osmPath, props.vehicleType);
            MemoryUsage.printCurrentMemUsage("[after init graph hopper]");
            graph.build(props);
            MemoryUsage.printCurrentMemUsage("[after build tor graph]");
        }

        TorSaver saver = new TorSaver(graph, setting);
        TrajReader reader = new TrajReader(props);
        mapper = Mappers.getMapper(props.mmAlg, graph);

        //readBatch trajectory data in batch from file
        List<Trajectory<TrajEntry>> rawTrajs = new LinkedList<>();
        while ( !reader.readBatch(props.trajSrcPath, null, rawTrajs)) {

            MemoryUsage.printCurrentMemUsage("[after loading trajectories]");

            //do map-matching
            List<Trajectory<TowerVertex>> mappedTrajectories = mapper.batchMatch(rawTrajs);

            //asyncSave data
            saver.asyncSave(mappedTrajectories, rawTrajs, false);
        }

        //do map-matching for the rest
        List<Trajectory<TowerVertex>> mappedTrajectories = mapper.batchMatch(rawTrajs);
        saver.asyncSave(mappedTrajectories, rawTrajs, true);
    }



//    /**
//     * todo
//     *
//     * In addition to required files, you can pass your timestamp file along to mm.
//     * Time stamp provides information that would help in the query.txt.
//     * It won't make difference in trajectory map-matching.
//     *
//     */
//    private static MapMatching build(String trajSrc, String dateSrc, String PBFPath, String outDir) {
//
//        MapMatching mm = MapMatching.build(trajSrc, PBFPath, outDir);
//
//        //check date file
//        File dateFile = new File(dateSrc);
//        if (!dateFile.exists()) {
//            logger.error("{} does not exist", dateSrc);
//            System.exit(-1);
//        }
//
//        mm.datePath = dateSrc;
//
//        return mm;
//    }

    /**
     * Builder to build mapMatching object.
     */
    public static class Builder {

        MMProperties props = new MMProperties();

        /**
         * @param algorithm the algorithm used in map-matching
         * @see Torch.Algorithms
         */
        public Builder setMapMatchingAlgorithm(String algorithm){
            props.mmAlg = algorithm;
            return this;
        }

        /**
         * @param vehicle It tells osm parser which edges and vertices to build.
         *                As car and bike do not run on same roads
         * @see Torch.vehicleType
         */
        public Builder setVehicleType(String vehicle){
            props.vehicleType = vehicle;
            return this;
        }

        /**
         * @param range This param will be used if the algorithm used is Torch.Algorithms.HMM_PRECOMPUTED.
         *              It tells the range to compute shortest path information between src and its near points.
         * @see TorDijkstra#run(TowerVertex)
         */
        public Builder setPrecomputationRange(int range){
            props.preComputationRange = range;
            return this;
        }

        /**
         * T-Torch will build and process trajectories in batch as sometimes dataset is too large to fit into memory at once
         * This tell T-Torch how many trajectories should be loaded and processed at once.
         *
         * @see TrajReader
         */
        public Builder setBatchSize(int batchSize){
            props.batchSize = batchSize;
            return this;
        }
        
        public Builder setBaseDir(String name){
            props.baseDir = name;
            return this;
        }

        /**
         * construct an MapMatching object with required files.
         *
         * @param trajSrcPath raw trajectory file with following format
         *                1    [[39.92123, 116.51172],[39.93883, 116.51135],[39.91034, 116.51627]]
         *                1 represents the hash of current trajectory.
         *                separated by \t character, a tuple of gps coordinates( lat, lng) that defines the trajectory.
         *
         * @param osmPath map data in format of *.osm.pbf
         * @return mapMatching object
         */
        public MapMatching build(String trajSrcPath, String osmPath){

            props.trajSrcPath = trajSrcPath;
            props.osmPath = osmPath;

            MapMatching mm = new MapMatching(new MMProperties(props));
            props.reset();
            return mm;
        }
    }
}
