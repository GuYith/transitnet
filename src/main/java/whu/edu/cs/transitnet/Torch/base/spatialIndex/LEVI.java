package whu.edu.cs.transitnet.Torch.base.spatialIndex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whu.edu.cs.transitnet.Torch.base.FileSetting;
import whu.edu.cs.transitnet.Torch.base.TopKQueryIndex;
import whu.edu.cs.transitnet.Torch.base.WindowQueryIndex;
import whu.edu.cs.transitnet.Torch.base.db.TrajVertexRepresentationPool;
import whu.edu.cs.transitnet.Torch.base.helper.GeoUtil;
import whu.edu.cs.transitnet.Torch.base.invertedIndex.VertexInvertedIndex;
import whu.edu.cs.transitnet.Torch.base.model.Coordinate;
import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
import whu.edu.cs.transitnet.Torch.base.model.Trajectory;
import whu.edu.cs.transitnet.Torch.mapMatching.model.TowerVertex;
import whu.edu.cs.transitnet.Torch.queryEngine.model.Circle;
import whu.edu.cs.transitnet.Torch.queryEngine.model.Geometry;
import whu.edu.cs.transitnet.Torch.queryEngine.model.LightEdge;
import whu.edu.cs.transitnet.Torch.queryEngine.model.SearchWindow;
import whu.edu.cs.transitnet.Torch.queryEngine.query.TrajectoryResolver;
import whu.edu.cs.transitnet.Torch.queryEngine.similarity.SimilarityFunction;

import java.util.*;

import static whu.edu.cs.transitnet.Torch.queryEngine.similarity.SimilarityFunction.MeasureType;

/**
 * ﻿LEVI stands for Lightweight edge & vertex vertexInvertedIndex.<p>
 * LEVI basically has two parts: grid vertexInvertedIndex and inverted vertexInvertedIndex on vertex.
 * This two level indexes structure supports range query.txt as well as top k query.txt over vertices.
 */
public class LEVI implements WindowQueryIndex, TopKQueryIndex {

    public int epsilon = 50; // within which range the point are considered match in parameter based similarity function
    private static final int THETA = 10; // within which querySpan the point in one sequence are considered to match the point in the other sequence.
    private static final int INITIAL_ROUND_FOR_DTW = 4;
    private static final int INITIAL_ROUND_FOR_H_OR_F = 5;

    private FileSetting setting;
    private static Logger logger = LoggerFactory.getLogger(LEVI.class);
    private VertexInvertedIndex vertexInvertedIndex;
    private VertexGridIndex gridIndex;

    private SimilarityFunction.MeasureType measureType;
    private SimilarityFunction<TrajEntry> similarityFunction = SimilarityFunction.DEFAULT;
    private TrajVertexRepresentationPool pool;
    private Map<Integer, TowerVertex> idVertexLookup;
    
    public LEVI(VertexInvertedIndex vertexInvertedIndex, VertexGridIndex gridIndex,
                SimilarityFunction.MeasureType measureType, TrajVertexRepresentationPool pool, Map<Integer, TowerVertex> idVertexLookup, FileSetting setting){

        this.setting = setting;
        this.vertexInvertedIndex = vertexInvertedIndex;
        this.gridIndex = gridIndex;
        this.measureType = measureType;
        this.pool = pool;
        this.idVertexLookup = idVertexLookup;
    }

    @Override
    public boolean build(String Null) {
        
        if (!vertexInvertedIndex.loaded) vertexInvertedIndex.build(setting.VERTEX_INVERTED_INDEX);
        if (!gridIndex.loaded) gridIndex.build(setting.GRID_INDEX);
        
        return vertexInvertedIndex.loaded && gridIndex.loaded;
    }
    
    @Override
    public boolean useEdge() {
        return false;
    }


    //todo
    @Override
    public List<String> findInRange(Geometry geometry) {

        Collection<Integer> points;
        if (geometry instanceof SearchWindow)
            points = gridIndex.pointsInWindow((SearchWindow) geometry);
        else if (geometry instanceof Circle)
            points = gridIndex.pointsInRange((Circle) geometry);
        else {
            logger.error("no such geometry defined");
            throw new IllegalStateException("no such geometry defined");
        }

        Set<String> ret = new HashSet<>();
        logger.debug("number of points in window: {}", points.size());

        for (Integer pointId : points)
            ret.addAll(vertexInvertedIndex.getKeys(pointId));
        logger.debug("number of trajectories in window: {}", ret.size());
        return new ArrayList<>(ret);
    }

    @Override
    public <T extends TrajEntry> List<String> findTopK(int k, List<T> pointQuery, List<LightEdge> edgeQuery, TrajectoryResolver resolver){

        if (measureType == MeasureType.DTW)
            return topKwithDTW(k, pointQuery, resolver);

        if (measureType == MeasureType.Frechet||
                measureType == MeasureType.Hausdorff)
            return topKwithFrechetOrHausdorff(k, pointQuery, resolver);

        if (measureType == MeasureType.LCSS)
            return topKwithLCSS(k, pointQuery, resolver);

        if (measureType == MeasureType.EDR)
            return topKwithEDR(k, pointQuery, resolver);

        logger.error("unsupported similarity measure: {}", measureType.toString());
        throw new IllegalStateException("unsupported similarity measure");
    }

    private <T extends TrajEntry> List<String> topKwithEDR(int k, List<T> pointQuery, TrajectoryResolver resolver){
        Map<String, Integer> trajUpperBound = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Map<String, int[]> cache = new HashMap<>();
        int querySize = pointQuery.size();

        for (int i = 0; i < pointQuery.size(); i++) {
            Collection<Integer> idSet = gridIndex.pointsInRange(new Circle(new Coordinate(pointQuery.get(i).getLat(), pointQuery.get(i).getLng()), epsilon));
            for (Integer vertexId : idSet) {
                if (visited.contains(vertexId)) continue;
                List<String> trajs = vertexInvertedIndex.getKeys(vertexId);
                for (String trajId : trajs) {
                    if (!cache.containsKey(trajId))
                        cache.put(trajId, pool.get(trajId));
                    trajUpperBound.merge(trajId, 1, (a, b) -> a + b);
                }
            }
            visited.addAll(idSet);
        }

        // Pair.value contains the possible minimum number of edits.
        PriorityQueue<Pair> candidateHeap = new PriorityQueue<>((p1, p2)->(Double.compare(p1.score,p2.score)));
        // Pair.value contains actual number of edits
        PriorityQueue<Pair> topKHeap = new PriorityQueue<>((p1, p2)->(Double.compare(p2.score, p1.score)));

        for (Map.Entry<String, Integer> entry : trajUpperBound.entrySet())
            candidateHeap.add(new Pair(entry.getKey(), Math.max(querySize, cache.get(entry.getKey()).length) - entry.getValue()));
        logger.debug("number of candidates: {}", candidateHeap.size());

        int counter = 0;
        while(!candidateHeap.isEmpty()){

            Pair pair = candidateHeap.poll();

            String curTrajId = pair.trajectoryID;
            double curUpperBound = pair.score;
            if (!resolver.meetTimeConstrain(curTrajId)) continue;

            int[] trajectory = cache.get(curTrajId);
            if (trajectory == null) continue;

            Trajectory<TrajEntry> t = new Trajectory<>();
            for (int entry : trajectory) {
                t.add(idVertexLookup.get(entry));
            }

            double realMatch = similarityFunction.EditDistanceonRealSequence(t, (List<TrajEntry>) pointQuery);
            pair = new Pair(curTrajId, realMatch);

            if (topKHeap.size() < k) {
                topKHeap.offer(pair);
            }else{
                if (topKHeap.peek().score <= curUpperBound) {
                    logger.debug("current upper bound: {}, current real score: {}", curUpperBound, topKHeap.peek().score);
                    break;
                }
                if (topKHeap.peek().score > pair.score) {
                    topKHeap.poll();
                    topKHeap.offer(pair);
                }
            }
        }

        List<String> resIDList = new ArrayList<>();
        while (!topKHeap.isEmpty()) {
            resIDList.add(topKHeap.poll().trajectoryID);
        }

        return resIDList;
    }

    private <T extends TrajEntry> List<String> topKwithLCSS(int k, List<T> pointQuery, TrajectoryResolver resolver) {

        Map<String, Integer> trajUpperBound = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        for (int i = 0; i < pointQuery.size(); i++) {
            Collection<Integer> idSet = gridIndex.pointsInRange(new Circle(new Coordinate(pointQuery.get(i).getLat(), pointQuery.get(i).getLng()), epsilon));
            for (Integer vertexId : idSet) {
                if (visited.contains(vertexId)) continue;
                List<String> trajs = vertexInvertedIndex.getKeys(vertexId);
                for (String trajId : trajs)
                    trajUpperBound.merge(trajId, 1, (a, b) -> a + b);
            }
            visited.addAll(idSet);
        }

        PriorityQueue<Pair> candidateHeap = new PriorityQueue<>((p1, p2)->(Double.compare(p2.score, p1.score)));
        PriorityQueue<Pair> topKHeap = new PriorityQueue<>((p1, p2)->(Double.compare(p1.score, p2.score)));

        for (Map.Entry<String, Integer> entry : trajUpperBound.entrySet())
            candidateHeap.add(new Pair(entry.getKey(), entry.getValue()));
        logger.debug("number of candidates: {}", candidateHeap.size());

        int counter = 0;
        while(!candidateHeap.isEmpty()){

            Pair pair = candidateHeap.poll();

            String curTrajId = pair.trajectoryID;
            double curUpperBound = pair.score;
            if (!resolver.meetTimeConstrain(curTrajId)) continue;

            int[] trajectory = pool.get(curTrajId);
            if (trajectory == null)
                continue;

            Trajectory<TrajEntry> t = new Trajectory<>();
            for (int entry : trajectory) {
                t.add(idVertexLookup.get(entry));
            }

            double realMatch = similarityFunction.LongestCommonSubsequence(t, (List<TrajEntry>) pointQuery, THETA);
            pair = new Pair(curTrajId, realMatch);

            if (topKHeap.size() < k) {
                topKHeap.offer(pair);
            }else{
                if (topKHeap.peek().score >= curUpperBound) {
                    logger.debug("current upper bound: {}, current real score: {}", curUpperBound, topKHeap.peek().score);
                    break;
                }
                if (topKHeap.peek().score < pair.score) {
                    topKHeap.poll();
                    topKHeap.offer(pair);
                }
            }
        }

        List<String> resIDList = new ArrayList<>();
        while (!topKHeap.isEmpty()) {
            resIDList.add(topKHeap.poll().trajectoryID);
        }

        return resIDList;
    }

    private <T extends TrajEntry> List<String> topKwithDTW(int k, List<T> pointQuery, TrajectoryResolver resolver) {

        PriorityQueue<Pair> topKHeap = new PriorityQueue<>(Comparator.comparingDouble(p -> p.score));
        double bestKthSoFar, overallUnseenUpperBound;
        double[] unseenUpperBounds = new double[pointQuery.size()];
        int round = INITIAL_ROUND_FOR_DTW;
        Set<String> visitTrajectorySet = new HashSet<>();


        int check = 0;
        while (check == 0) {

            overallUnseenUpperBound = 0;

            //each query.txt point match with the nearest point of a trajectory,
            // and the lower bound is the maximum distance between a query.txt and existing points of a trajectory
            Map<String, Double> trajUpperBound = new HashMap<>();
            Map<String, Map<TrajEntry, Double>> trajUpperBoundForDTW = new HashMap<>();

            //findMoreVertices candiates incrementally and calculate their lower bound
            logger.info("finding and computing bound for candidate trajectories...");
            int querySize = pointQuery.size();
            for (int i = 0; i < querySize; i++) {

                TrajEntry queryVertex = pointQuery.get(i);

                double upperBound = gridIndex.findBound(queryVertex, round);
                unseenUpperBounds[i] = upperBound;
                overallUnseenUpperBound += upperBound;

                //findMoreVertices the nearest pair between a trajectory and query.txt queryVertex
                //trajectory hash, queryVertex hash vertices
                Set<Integer> vertices = new HashSet<>();
                if (round == INITIAL_ROUND_FOR_DTW) {
                    gridIndex.incrementallyFind(queryVertex, round, vertices, true);
                }else
                    gridIndex.incrementallyFind(queryVertex, round, vertices, false);
                for (Integer vertexId : vertices){
                    Double score = - GeoUtil.distance(idVertexLookup.get(vertexId), queryVertex);
                    List<String> l = vertexInvertedIndex.getKeys(vertexId);
                    for (String trajId : l) {
                        Map<TrajEntry, Double> map = trajUpperBoundForDTW.get(trajId);
                        if (map != null) {
                            if (!map.containsKey(queryVertex) ||
                                    score > map.get(queryVertex))
                                map.put(queryVertex, score);
                        } else {
                            map = trajUpperBoundForDTW.computeIfAbsent(trajId, key -> new HashMap<>());
                            map.put(queryVertex, score);
                        }
                    }
                }
            }

            for (Map.Entry<String, Map<TrajEntry, Double>> entry: trajUpperBoundForDTW.entrySet()) {
                String trajId = entry.getKey();
                Map<TrajEntry, Double> map = entry.getValue();
                double score = 0.;
                for (int i = 0; i < querySize; i++){
                    TrajEntry cur = pointQuery.get(i);
                    if (map.containsKey(cur))
                        score += map.get(cur);
                    else
                        score += unseenUpperBounds[i];
                }
                trajUpperBound.put(trajId, score);
            }

            //rank trajectories by their upper bound
            PriorityQueue<Map.Entry<String, Double>> rankedCandidates = new PriorityQueue<>((e1,e2) -> Double.compare(e2.getValue(),e1.getValue()));

            for (Map.Entry<String, Double> entry : trajUpperBound.entrySet()) {
                if (!visitTrajectorySet.contains(entry.getKey()))
                    rankedCandidates.add(entry);
            }
            //mark visited trajectories
            visitTrajectorySet.addAll(trajUpperBound.keySet());
            logger.info( "total number of candidate trajectories in {}th round: {}", round, rankedCandidates.size());

            //calculate exact distance for each candidate
            int j = 0;
            while (!rankedCandidates.isEmpty()) {

                Map.Entry<String, Double> entry1 = rankedCandidates.poll();
                String curTrajId = entry1.getKey();
                double curUpperBound = entry1.getValue();
                if (!resolver.meetTimeConstrain(curTrajId)) continue;

                int[] trajectory = pool.get(curTrajId);
                if (trajectory == null) continue;

                Trajectory<TrajEntry> t = new Trajectory<>();
                for (int entry : trajectory) {
                    t.add(idVertexLookup.get(entry));
                }

                double realDist = 0;

                realDist = similarityFunction.DynamicTimeWarping(t, (List<TrajEntry>) pointQuery);

                double score = -realDist;

                Pair pair = new Pair(curTrajId, score);
                if (topKHeap.size() < k) {
                    topKHeap.offer(pair);
                }else{

                    if (topKHeap.peek().score < pair.score) {
                        topKHeap.offer(pair);
                        topKHeap.poll();
                    }

                    bestKthSoFar = topKHeap.peek().score;

                    if (++j % 1500 == 0 || bestKthSoFar > curUpperBound)
                        logger.info("have processed {} trajectories, current {}th trajectory upper bound: {}, " +
                                        "top kth trajectory real score: {}, current unseen trajectory upper bound: {}",
                                j + 1, j, curUpperBound, bestKthSoFar, overallUnseenUpperBound);

                    if (bestKthSoFar > overallUnseenUpperBound)
                        check = 1;

                    if (bestKthSoFar > curUpperBound)
                        break;
                }
            }

            if (round == 7) {
                logger.error("round = 7, too much rounds");
                break;
            }
            ++round;
        }


        List<String> resIDList = new ArrayList<>();
        while (!topKHeap.isEmpty()) {
            resIDList.add(topKHeap.poll().trajectoryID);
        }

        return resIDList;
    }

    private <T extends TrajEntry> List<String> topKwithFrechetOrHausdorff(int k, List<T> pointQuery, TrajectoryResolver resolver) {
        logger.debug("k: {}", k);

        PriorityQueue<Pair> topKHeap = new PriorityQueue<>(Comparator.comparingDouble(p -> p.score));
        double bestKthSoFar = - Double.MAX_VALUE, overallUnseenUpperBound;
        double[] unseenUpperBounds = new double[pointQuery.size()];

        int round = INITIAL_ROUND_FOR_H_OR_F;
        Set<String> visitTrajectorySet = new HashSet<>();


        int check = 0;
        while (check == 0) {

            overallUnseenUpperBound = Double.MAX_VALUE;

            //each query.txt point match with the nearest point of a trajectory,
            // and the lower bound is the maximum distance between a query.txt and existing points of a trajectory
            Map<String, Double> trajUpperBound = new HashMap<>();
            Map<String, Map<TrajEntry, Double>> trajUpperBoundDetailed = new HashMap<>();

            //findMoreVertices candiates incrementally and calculate their lower bound
            logger.info("finding and computing bound for candidate trajectories...");
            int querySize = pointQuery.size();
            for (int i = 0; i < querySize; i++) {

                TrajEntry queryVertex = pointQuery.get(i);

                double upperBound = gridIndex.findBound(queryVertex, round);
                unseenUpperBounds[i] = upperBound;
                overallUnseenUpperBound = Math.min(upperBound, overallUnseenUpperBound);

                //findMoreVertices the nearest pair between a trajectory and query.txt queryVertex
                //trajectory hash, queryVertex hash vertices
                Set<Integer> vertices = new HashSet<>();

                if (round == INITIAL_ROUND_FOR_H_OR_F) gridIndex.incrementallyFind(queryVertex, round, vertices, true);
                else gridIndex.incrementallyFind(queryVertex, round, vertices, false);

                for (Integer vertexId : vertices) {
                    Double score = -GeoUtil.distance(idVertexLookup.get(vertexId), queryVertex);
                    List<String> l = vertexInvertedIndex.getKeys(vertexId);
                    for (String trajId : l) {
                        Map<TrajEntry, Double> map = trajUpperBoundDetailed.get(trajId);
                        if (map != null) {
                            if (!map.containsKey(queryVertex) ||
                                    score > map.get(queryVertex))
                                map.put(queryVertex, score);
                        } else {
                            map = trajUpperBoundDetailed.computeIfAbsent(trajId, key -> new HashMap<>());
                            map.put(queryVertex, score);
                        }
                    }
                }
            }

            for (Map.Entry<String, Map<TrajEntry, Double>> entry: trajUpperBoundDetailed.entrySet()) {
                String trajId = entry.getKey();

                Map<TrajEntry, Double> map = entry.getValue();
                double score = Double.MAX_VALUE;
                for (int i = 0; i < querySize; i++){
                    TrajEntry cur = pointQuery.get(i);
                    if (map.containsKey(cur))
                        score = Math.min(map.get(cur), score);
                    else
                        score = Math.min(unseenUpperBounds[i], score);
                }
                trajUpperBound.put(trajId, score);
            }

            //rank trajectories by their upper bound
            PriorityQueue<Map.Entry<String, Double>> rankedCandidates = new PriorityQueue<>((e1,e2) -> Double.compare(e2.getValue(),e1.getValue()));

            for (Map.Entry<String, Double> entry : trajUpperBound.entrySet()) {
                if (!visitTrajectorySet.contains(entry.getKey()))
                    rankedCandidates.add(entry);
            }
            //mark visited trajectories
            visitTrajectorySet.addAll(trajUpperBound.keySet());
            logger.info( "total number of candidate trajectories in {}th round: {}", round, rankedCandidates.size());

            //calculate exact distance for each candidate
            int j = 0;
            while (!rankedCandidates.isEmpty()) {

                Map.Entry<String, Double> entry1 = rankedCandidates.poll();
                String curTrajId = entry1.getKey();
                double curUpperBound = entry1.getValue();
                if (!resolver.meetTimeConstrain(curTrajId)) continue;

                int[] trajectory = pool.get(curTrajId);
                if (trajectory == null)
                    continue;
                Trajectory<TrajEntry> t = new Trajectory<>();
                for (int entry : trajectory) {
                    t.add(idVertexLookup.get(entry));
                }

                double realDist = 0;
                switch (measureType) {
                    case Hausdorff:
                        realDist = similarityFunction.Hausdorff(t, (List<TrajEntry>)pointQuery);
                        break;
                    case Frechet:
                        realDist = similarityFunction.Frechet(t, (List<TrajEntry>)pointQuery);
                        break;
                }

                double score = -realDist;

                Pair pair = new Pair(curTrajId, score);
                if (topKHeap.size() < k) {
                    topKHeap.offer(pair);
                }else{

                    if (topKHeap.peek().score < pair.score) {
                        topKHeap.offer(pair);
                        topKHeap.poll();
                    }

                    bestKthSoFar = topKHeap.peek().score;

                    if (++j % 1500 == 0 || bestKthSoFar > curUpperBound)
                        logger.info("have processed {} trajectories, current {}th trajectory upper bound: {}, " +
                                        "top kth trajectory real score: {}, current unseen trajectory upper bound: {}",
                                j + 1, j, curUpperBound, bestKthSoFar, overallUnseenUpperBound);

                    if (bestKthSoFar > overallUnseenUpperBound)
                        check = 1;

                    if (bestKthSoFar > curUpperBound)
                        break;
                }
            }

            logger.info("round: {}, kth score: {}, unseen bound: {}", round, bestKthSoFar, overallUnseenUpperBound);

            if (round == 7) {
                logger.error("round = 7, too much rounds");
                break;
            }
            ++round;
        }


        List<String> resIDList = new ArrayList<>();
        while (!topKHeap.isEmpty()) {
            resIDList.add(topKHeap.poll().trajectoryID);
        }

        return resIDList;
    }

    public void updateMeasureType(MeasureType measureType) {
        this.measureType = measureType;
    }

    public void updateEpsilon(int epsilon) {
        this.epsilon = epsilon;
        similarityFunction.comparator = (p1, p2) -> {
            double dist = GeoUtil.distance(p1, p2);
            if (dist <= epsilon) return 0;
            return 1;
        };
    }


    static class Pair {
        final String trajectoryID;
        final double score;

        Pair(String trajectoryID, double score) {
            this.trajectoryID = trajectoryID;
            this.score = score;
        }

        @Override
        public String toString(){
            return "{"+trajectoryID+": "+score+"}";
        }
    }
}
