package whu.edu.cs.transitnet.Torch.base.spatialIndex;

import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whu.edu.cs.transitnet.Torch.base.FileSetting;
import whu.edu.cs.transitnet.Torch.base.TopKQueryIndex;
import whu.edu.cs.transitnet.Torch.base.WindowQueryIndex;
import whu.edu.cs.transitnet.Torch.base.helper.GeoUtil;
import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.model.Trajectory;
import whu.edu.cs.transitnet.Torch.queryEngine.model.SearchWindow;

import java.io.*;
import java.util.*;

/**
 * divide trajectories into envelope and use R tree to dataStructure them
 *
 */
//todo
public abstract class RTreeWrapper implements WindowQueryIndex, TopKQueryIndex {

    private static Logger logger = LoggerFactory.getLogger(RTreeWrapper.class);

    private static final double MAX_LENGTH = 500;
    private FileSetting setting;

    /*
     * This is the threashold for number of points in MBR( Envelope)
     * If a trajectory contains more than 10 points,
     * it will be sliced into multiple MBR( Envelope)
     */
    private static final int POINT_NUMBER_IN_MBR = 10;

    private RTree<String, Geometry> rTree;

    public RTreeWrapper(FileSetting setting){
        this.setting = setting;
        this.rTree = RTree.star().maxChildren(6).create();
    }

    public List<String> findInRange(SearchWindow window) {

        rx.Observable<Entry<String, Geometry>> entries = this.rTree.search(
                Geometries.rectangleGeographic(window.leftLng, window.lowerLat, window.rightLng, window.upperLat));

        Set<String> trajIDSet = new HashSet<>();
        entries.forEach(entry -> {
            trajIDSet.add(entry.value());
        });

//        List<Trajectory<Coordinate>> resolvedRet = new ArrayList<>();
//        trajIDSet.forEach(trajId -> {
//            resolvedRet.add(trajectoryMap.getList(trajId));
//        });

        return new ArrayList<>(trajIDSet);
    }


//    public <T extends TorPoint> List<String> findTopK(int k, List<T> query.txt, List<LightEdge> edgeQuery) {
//        if (this.rTree == null)
//            throw new IllegalArgumentException("call build() first");
//
//        TorPoint cur = query.txt.getList(0);
//        double lowerLat = cur.getLat(), minLon = cur.getLng(), upperLat = cur.getLat(), maxLon = cur.getLng();
//        double pointNumber = 0;
//
//        //key for trajectory id, value for MBR
//        Set<String> trajIdSet = new HashSet<>();
//        List<Rectangle> queryMBR = new ArrayList<>();
//        for (int i = 1; i < query.txt.size(); ++i) {
//            cur = query.txt.getList(i);
//            ++pointNumber;
//            if (pointNumber >= POINT_NUMBER_IN_MBR || i == query.txt.size() - 1) {
//                Rectangle mbr = Geometries.rectangleGeographic(minLon, lowerLat, maxLon, upperLat);
//                rx.Observable<Entry<String, Geometry>> results = this.rTree.search(mbr);
//                queryMBR.add(mbr);
//                findMBRs(results, trajIdSet);
//                lowerLat = cur.getLat();
//                minLon = cur.getLng();
//                upperLat = cur.getLat();
//                maxLon = cur.getLng();
//                pointNumber = 0;
//                ++i;
//            }
//            if (cur.getLat() > upperLat) upperLat = cur.getLat();
//            if (cur.getLat() < lowerLat) lowerLat = cur.getLat();
//            if (cur.getLng() > maxLon) maxLon = cur.getLng();
//            if (cur.getLng() < minLon) minLon = cur.getLng();
//        }
//
//
//        rx.Observable<Entry<String, Geometry>> results = this.rTree.search(
//                Geometries.rectangleGeographic(minLon, lowerLat, maxLon, upperLat));
//        findMBRs(results, trajIdSet);
//
//        //find real candidate
//        PriorityQueue<Pair> rankedCandidates = new PriorityQueue<>(Comparator.comparingDouble(p -> p.score));
//        for (String id : trajIdSet) {
//            List<Rectangle> candidateMBR = trajectoryMBRMap.getList(id);
//            double lowerBound = calLowerBound(queryMBR, candidateMBR);
//            rankedCandidates.add(new Pair(id, lowerBound));
//        }
//
//        //calculate the distance between the trajectory and the query.txt
//        PriorityQueue<Pair> topkHeap = new PriorityQueue<>((p1, p2) -> Double.compare(p2.score, p1.score));
//        SimilarityFunction<TorPoint> getSimilarityMeasure = SimilarityFunction.DEFAULT;
//        double bestSoFar = 0;
//
//        while (!rankedCandidates.isEmpty()) {
//            Pair pair = rankedCandidates.poll();
//            if (pair.score > bestSoFar && topkHeap.size() >= k) break;
//            pair.score = getSimilarityMeasure.fastDynamicTimeWarping(trajectoryMap.getList(pair.trajectoryID), (List<TorPoint>)query.txt, 10, bestSoFar);
//            topkHeap.add(pair);
//            if (topkHeap.size() > k) topkHeap.poll();
//        }
//
//        //return the results
//        List<String> resIDList = new ArrayList<>();
//        while (topkHeap.size() > 0) {
//            resIDList.add(topkHeap.poll().trajectoryID);
//        }
//        return resIDList;
//        return null;
//    }

    private void findMBRs(rx.Observable<Entry<String, Geometry>> results, Set<String> trajectoryID) {
        results.forEach(entry -> {
            trajectoryID.add(entry.value());
        });
    }

    private double calLowerBound(List<Rectangle> q, List<Rectangle> r) {

        if (q.size() == 0 || r.size() == 0)
            throw new IllegalArgumentException("q size: " + q.size() + ", r size: " + r.size() + " cannot be zero.");

        double lowerBound = 0;

        if (q.size() < r.size()) {
            List<Rectangle> temp = q;
            q = r;
            r = temp;
        }

        for (int i = 0; i < r.size(); ++i) {
            lowerBound += calDistanceBtwRect(q.get(i), r.get(i));
        }

        for (int i = r.size(); i < q.size(); ++i) {
            lowerBound += calDistanceBtwRect(q.get(i), r.get(r.size() - 1));
        }

        return lowerBound;
    }

    private double calDistanceBtwRect(Rectangle q, Rectangle r) {
        float[] qlon = new float[]{q.x1(), q.x2()};
        float[] qlat = new float[]{q.y1(), q.y2()};

        float[] rlon = new float[]{r.x1(), r.x2()};
        float[] rlat = new float[]{r.y1(), r.y2()};

        double minDistance = Double.MAX_VALUE;
        for (float lon1 : qlon) {
            for (float lat1 : qlat) {
                for (float lon2 : rlon) {
                    for (float lat2 : rlat) {
                        double distance = GeoUtil.distance(lat1, lat2, lon1, lon2);
                        if (distance < minDistance) minDistance = distance;
                    }
                }
            }
        }
        return minDistance;
    }

    private void calUpperBound(rx.Observable<Entry<Integer, Geometry>> results, Map<Integer, Double> idScores, final double pointNumber) {
        results.forEach(entry -> {
            idScores.merge(entry.value(), pointNumber, (a, b) -> b + a);
        });
    }

    /**
     * If the RTree dataStructure file exists, the subroutine will build them into RTree.
     * Otherwise it will buildTorGraph it using other data file on disk.
     *
     * @see #build() Build RTree from memory data.
     */
    public void build() {
        load();
    }

    private void load(){

        logger.info("build rtree");
        Serializer<String, Geometry> serializer = Serializers.flatBuffers().javaIo();
        File rtree = new File(setting.RTREE_INDEX);

        try (InputStream is = new FileInputStream(rtree)) {
            this.rTree = serializer.read(is, rtree.length(), InternalStructure.DEFAULT);
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }

    public <T extends TrajEntry> void indexAll(List<Trajectory<T>> trajectories){

        Iterator<Trajectory<T>> trajectoryIterator = trajectories.iterator();

        while (trajectoryIterator.hasNext()) {

            Trajectory<T> trajectory = trajectoryIterator.next();
            T pre = trajectory.get(0);

            double minLat = pre.getLat(), minLng = pre.getLng(), maxLat = pre.getLat(), maxLng = pre.getLng();
            double length = 0.0;
            int counter = 0;
            for (int i = 1; i < trajectory.size(); ++i) {
                double dist = GeoUtil.distance(pre, trajectory.get(i));
                pre = trajectory.get(i);
                length += dist;
                if (length >= MAX_LENGTH) {

                    rTree = rTree.add(trajectory.id, Geometries.rectangleGeographic(minLng, minLat, maxLng, maxLat));

                    if (counter++ % 100000 == 0)
                        System.out.println(counter);

                    minLat = pre.getLat();
                    minLng = pre.getLng();
                    maxLat = pre.getLat();
                    maxLng = pre.getLng();
                    length = 0.0;
                    ++i;
                }
                if (pre.getLat() > maxLat) maxLat = pre.getLat();
                if (pre.getLat() < minLat) minLat = pre.getLat();
                if (pre.getLng() > maxLng) maxLng = pre.getLng();
                if (pre.getLng() < minLng) minLng = pre.getLng();
            }

            rTree = rTree.add(trajectory.id, Geometries.rectangleGeographic(minLng, minLat, maxLng, maxLat));
        }
    }

    public void Save(){
        logger.info("start to serialize dataStructure file to disk");

        File rtree = new File(setting.RTREE_INDEX);
        if (rtree.exists())
            rtree.delete();

        Serializer<String, Geometry> serializer = Serializers.flatBuffers().javaIo();
        try (OutputStream os = new FileOutputStream(rtree)) {
            serializer.write(this.rTree, os);
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    class Pair {
        final String trajectoryID;
        double score;

        Pair(String trajectoryID, double score) {
            this.score = score;
            this.trajectoryID = trajectoryID;
        }
    }
}

