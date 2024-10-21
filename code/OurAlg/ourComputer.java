package OurAlg;

import Tools.*;

import java.util.*;

import static Tools.tools.distance;


public class ourComputer {

    public static int k;
    public static ArrayList<Point> pointList;
    public static ArrayList<ArrayList<Integer>> cannotLinkSet, mustLinkSet;
    static float[] RMark;
    static float R, errorBar, Rmax, Rmin;
    static ArrayList<Integer> must = new ArrayList<>(), centers;

    public ourComputer(ArrayList<Point> pointList, ArrayList<ArrayList<Integer>> CannotList, ArrayList<ArrayList<Integer>> MustList, int k, float[] RMark) {
        this.pointList = pointList;
        cannotLinkSet = CannotList;
        mustLinkSet = MustList;
        this.k = k;
        this.RMark = RMark;
    }

    // Method to set clusterID for centers
    private void setClusterIDs(List<Integer> centers) {
        for (int i = 0; i < centers.size(); i++) {
            pointList.get(centers.get(i)).setClusterID(i);
        }
    }

    public float[] vertexCover() {
        System.out.println("vertexCover:___________________________________________");

        Rmax = RMark[0];
        R = Rmax;
        Rmin = RMark[1];
        errorBar = RMark[2];
        centers = new ArrayList<>();
        int N = pointList.size();
        float[] accuracy;

        long startTime = System.nanoTime();
        algorithm();
        long endTime = System.nanoTime();
        double times = endTime - startTime;

        accuracy = Metrics.metrics(N, pointList, centers, k, times + "");

        return accuracy;
    }

    // Method to find the candidate center set
    private List<List<Integer>> findCandidateCenterSet() {
        List<List<Integer>> candidateCenterSet = new ArrayList<>();
        int maximumSize = cannotLinkSet.stream().mapToInt(List::size).max().orElse(0);
        cannotLinkSet.stream()
                .filter(cannotLink -> cannotLink.size() == maximumSize)
                .forEach(candidateCenterSet::add);
        return candidateCenterSet;
    }

    // Method to initialize center
    private List<Integer> initializeCenter(List<List<Integer>> candidateCenterSet, Random r) {
        List<Integer> initCenter = new ArrayList<>();
        if (candidateCenterSet.isEmpty()) {
            initCenter.add(r.nextInt(pointList.size()));
        } else {
            initCenter.addAll(candidateCenterSet.get(r.nextInt(candidateCenterSet.size())));
        }
        return initCenter;
    }

    // Method to perform binary search to ensure R
    private void performBinarySearch(List<Integer> initCenter) {
        boolean flag = false;
        while (Rmax - Rmin > errorBar || !flag) {
            R = (Rmin + Rmax) / 2.0f;
            if (R == Rmin || R == Rmax) {
                Rmin = Rmax;
                R = Rmax;
            }
            flag = true;
            centers.clear();
            centers.addAll(initCenter);
            updateMLinCenter();

            // Exchange the center
            exchangeCenter();
            if (centers.size() <= k) {
                farthestPoint(true);
            }

            if (centers.size() > k || !judgeCannot(R)) {
                flag = false;
            }

            if (flag) {
                Rmax = (Rmin + Rmax) / 2.0f;
            } else {
                Rmin = (Rmin + Rmax) / 2.0f;
            }
        }

    }


    // Method to exchange centers
    private void exchangeCenter() {
        while (!judgeCannot(R)) {
            for (List<Integer> cannotLink : cannotLinkSet) {
                List<Integer> repeatLeft = new ArrayList<>();
                List<Integer> repeatRight = new ArrayList<>();
                processCannotLinks(cannotLink, repeatLeft, repeatRight);
                centers.removeAll(repeatLeft);
                cannotLink.removeAll(repeatRight);

                if (!cannotLink.isEmpty()) {
                    RDS hk = new RDS();
                    hk.input(pointList, centers, cannotLink, R, mustLinkSet, must);
                }
                centers.addAll(repeatLeft);
                cannotLink.addAll(repeatRight);
            }

            if (centers.size() > k) {
                break;
            }
        }
    }

    private void processCannotLinks(List<Integer> cannotLink, List<Integer> repeatLeft, List<Integer> repeatRight) {

        for (int e : cannotLink) {
            Point point = pointList.get(e);
            int mustID = point.getMustID();
            if (centers.contains(e)) {
                repeatLeft.add(e);
                repeatRight.add(e);
            } else if (must.contains(mustID)) {
                repeatRight.add(e);
                centers.stream()
                        .filter(centerID -> pointList.get(centerID).getMustID() == mustID)
                        .forEach(repeatLeft::add);
            }
        }
    }

    // Method to update must list
    private void updateMLinCenter() {
        must.clear();
        centers.stream()
                .map(centerID -> pointList.get(centerID).getMustID())
                .filter(mustID -> mustID != -1)
                .forEach(must::add);
    }

    // Method to update centers
    private int farthestPoint(boolean addCenter) {
        int farPID = -1;
        float farPdistance = -1;

        for (int i = 0; i < pointList.size(); i++) {
            Point currentPoint = pointList.get(i);
            int mustID = currentPoint.getMustID();
            List<Integer> linkedPoints = mustID != -1 ? mustLinkSet.get(mustID) : Collections.emptyList(); //所属mustlink 集合

            if (currentPoint.getConID() == -1 && !must.contains(mustID)) {
                int mark = i;
                float minDist = Float.MAX_VALUE;

                if (mustID != -1) {
                    for (int center : centers) {
                        float maxDist = 0;
                        int markMaxDist = -1;
                        for (int linkedPointID : linkedPoints) {
                            float dist = distance(pointList.get(center), pointList.get(linkedPointID));
                            if (maxDist < dist) {
                                maxDist = dist;
                                markMaxDist = linkedPointID;
                            }
                        }
                        if (minDist > maxDist) {
                            minDist = maxDist;
                            mark = markMaxDist;
                        }
                    }
                } else {
                    minDist = centers.stream()
                            .map(center -> distance(currentPoint, pointList.get(center)))
                            .min(Float::compare)
                            .orElse(Float.MAX_VALUE);
                }

                if (!addCenter && minDist > farPdistance) {
                    farPdistance = minDist;
                    farPID = mark;
                } else if (addCenter && minDist > R) {
                    centers.add(mark);
                    Optional.of(mustID)
                            .filter(id -> id != -1)
                            .ifPresent(must::add);
                }
            }
        }
        return farPID;
    }

    private void algorithm() {
        // Find the center
        List<List<Integer>> candidateCenterSet = new ArrayList<>();
        candidateCenterSet = findCandidateCenterSet();

        Random r = new Random();
        List<Integer> initCenter = initializeCenter(candidateCenterSet, r);

        if (k == initCenter.size()) {
            centers.addAll(initCenter);
        } else {
            performBinarySearch(initCenter);// Ensure R: binary search
            while (centers.size() < k) {
                centers.add(farthestPoint(false));
            }
        }

        setClusterIDs(centers);
        updateMLinCenter();
        assignGene();
        assignMust();
        assignCannot();
    }


    private boolean judgeCannot(float inputR) {
        boolean flag = true;
        for (List<Integer> cannotLink : cannotLinkSet) {
            List<Integer> pointLeft = new ArrayList<>(centers);
            List<Integer> pointRight = new ArrayList<>(cannotLink);
            List<Integer> repeatLeft = new ArrayList<>();
            List<Integer> repeatRight = new ArrayList<>();
            processCannotLinks(cannotLink, repeatLeft, repeatRight);
            pointLeft.removeAll(repeatLeft);
            pointRight.removeAll(repeatRight);

            flag = judgeCannotOnce(inputR, pointLeft, pointRight);
            if (!flag) {
                return false;
            }
        }
        return flag;
    }


    private boolean judgeCannotOnce(float inputR, List<Integer> pointsLeft, List<Integer> pointsRight) {

        //for cannot-link:add center
        matching2 minMaxMatchingNew = new matching2(pointList, pointsLeft, pointsRight, inputR, mustLinkSet);
        return minMaxMatchingNew.searchcount() >= pointsRight.size();
    }


    private void assignMust() {

        for (int mustID : must) {
            for (int linkedPointID : mustLinkSet.get(mustID)) {
                centers.stream()
                        .filter(centerID -> pointList.get(centerID).getMustID() == mustID)
                        .findFirst()
                        .ifPresent(centerID -> pointList.get(linkedPointID).setClusterID(pointList.get(centerID).getClusterID()));
            }
        }


        for (int j = 0; j < mustLinkSet.size(); j++) {
            int mark = -1;
            if (!must.contains(j)) {
                float minDist = Float.MAX_VALUE;

                for (int l = 0; l < centers.size(); l++) {
                    float maxDist = 0;
                    Point centerPoint = pointList.get(centers.get(l));

                    for (int linkedPointID : mustLinkSet.get(j)) {
                        float tempDist  = Math.max(maxDist, distance(centerPoint, pointList.get(linkedPointID)));
                        if (centerPoint.getMustID() != -1){
                            for (int mustID : mustLinkSet.get(centerPoint.getMustID())) {
                                tempDist = Math.max(tempDist, distance(pointList.get(linkedPointID), pointList.get(mustID)));
                            }
                        }
                        maxDist = Math.max(maxDist, tempDist);
                    }
                    if (minDist > maxDist) {
                        minDist = maxDist;
                        mark = l;
                    }
                    for (int p_i : mustLinkSet.get(j)) {
                        Point p = pointList.get(p_i);
                        p.setClusterID(mark);
                    }
                }
            }
        }
    }

    private void assignCannot() {

        //for cannot-link:add center
        for (List<Integer> cannotLink : cannotLinkSet) {
            ArrayList<Integer> pointsLeft = new ArrayList<>(centers);
            ArrayList<Integer> pointsRight = new ArrayList<>(cannotLink);
            List<Integer> repeatLeft = new ArrayList<>();
            List<Integer> repeatRight = new ArrayList<>();
            processCannotLinks(cannotLink, repeatLeft, repeatRight);
            pointsLeft.removeAll(repeatLeft);
            pointsRight.removeAll(repeatRight);

            float inRmax = R;
            while (pointsRight.size() > 1) {
                float inRmin = 0;
                float inR = R;
                boolean conditionMet = false;
                while (inRmax - inRmin > errorBar || !conditionMet) {
                    inR = (inRmin + inRmax) / 2.0f;
                    if (inR == inRmax || inR == inRmin) {
                        inR = inRmax;
                    }
                    if (judgeCannotOnce(inR, pointsLeft, pointsRight)) {
                        inRmax = inR;
                        conditionMet = true;
                    } else {
                        inRmin = inR;
                        conditionMet = false;
                    }
                }
                matching2 MAlg = new matching2(pointList, pointsLeft, pointsRight, inR, mustLinkSet);
                MAlg.assignCannot(pointList, mustLinkSet);

                int markFar = 0;
                float farPointDist = 0;
                for (int j = 0; j < pointsRight.size(); j++) {
                    float dist = distance(pointList.get(pointsRight.get(j)), pointList.get(pointsLeft.get(MAlg.match[j])));
                    if (dist > farPointDist) {
                        markFar = j;
                        farPointDist = dist;
                    }
                }
                pointsLeft.remove(MAlg.match[markFar]);
                pointsRight.remove(markFar);
            }

            if (pointsRight.size() == 1) {
                Point rightPoint = pointList.get(pointsRight.get(0));
                int mustID = rightPoint.getMustID();

                float minDist = Float.MAX_VALUE;
                int mark = 0;

                for (int j = 0; j < pointsLeft.size(); j++) {
                    Point leftPoint = pointList.get(pointsLeft.get(j));
                    float dmax = distance(rightPoint, leftPoint);

                    if (mustID != -1) {
                        for (int linkedPointID : mustLinkSet.get(mustID)) {
                            Point linkedPoint = pointList.get(linkedPointID);
                            dmax = Math.max(dmax, distance(linkedPoint, leftPoint));
                        }
                    }
                    if (dmax < minDist) {
                        mark = j;
                        minDist = dmax;
                    }
                }
                int clusterID = pointList.get(pointsLeft.get(mark)).getClusterID();
                rightPoint.setClusterID(clusterID);
                if (mustID != -1) {
                    for (int linkedPointID : mustLinkSet.get(mustID)) {
                        pointList.get(linkedPointID).setClusterID(clusterID);
                    }
                }
            }
        }
    }

    private static void assignGene() {
        for (Point point : pointList) {
            if (point.getMustID() == -1 && point.getConID() == -1) {
                float mindistance = Float.MAX_VALUE;

                for (int l = 0; l < centers.size(); l++) {
                    float dist = tools.distance(pointList.get(centers.get(l)), point);
                    if (dist < mindistance) {
                        point.setClusterID(l);
                        mindistance = dist;
                    }
                }
            }
        }
    }

}