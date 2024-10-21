package Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static Tools.tools.distance;
import static javax.swing.UIManager.get;

//using min-max method to judge whether cover j points for the constrained set
public class matching2 {
    List<Integer> pointLeft, pointRight;   //need n*n graph to be adjacent matrix
    public int[] match;     //record the correspondent points. Supposing match[0]=1, that means the two points are matching
    boolean[][] line;
    boolean[] used;  //for each points of augmenting-path, recording to be queried

    public matching2(ArrayList<Point> pointlist, List<Integer> pointLeft, List<Integer> pointRight, float R, ArrayList<ArrayList<Integer>> mustLinks) {
        this.pointLeft = pointLeft;
        this.pointRight = pointRight;
        line = new boolean[pointLeft.size()][pointRight.size()];
        used = new boolean[pointRight.size()];
        match = new int[pointRight.size()];

        Arrays.fill(match, -1);
        Arrays.fill(used, false);

        for (int i = 0; i < pointLeft.size(); i++) {
            for (int j = 0; j < pointRight.size(); j++) {
                float maxdist = 0;
                if (pointlist.get(pointRight.get(j)).getMustID() != -1) {
                    for (int l = 0; l < mustLinks.get(pointlist.get(pointRight.get(j)).getMustID()).size(); l++) {
                        maxdist = Math.max(maxdist, distance(pointlist.get(pointLeft.get(i)), pointlist.get(mustLinks.get(pointlist.get(pointRight.get(j)).getMustID()).get(l))));
                    }
                } else {
                    maxdist = distance(pointlist.get(pointLeft.get(i)), pointlist.get(pointRight.get(j)));
                }

                if (maxdist <= R) line[i][j] = true;
            }
        }
    }

    public int searchcount() {
        int countM = 0;

        for (int i = 0; i < pointLeft.size(); i++) {
            Arrays.fill(used, false);
            findAugmentPath(i);
        }

        for (int value : match) {
            if (value != -1) countM++;
        }
        return countM;
    }

    //whether to find the augmenting-path.
    boolean findAugmentPath(int left) {
        for (int i = 0; i < pointRight.size(); i++) {
            if (left < line.length && !used[i] && line[left][i]) {
                used[i] = true;
                if (match[i] == -1 || findAugmentPath(match[i])) {
                    match[i] = left;
                    return true;
                }
            }
        }
        return false;
    }

    public void assignCannot(ArrayList<Point> pointList, ArrayList<ArrayList<Integer>> mustLinkSet) {

        for (int i = 0; i < pointLeft.size(); i++) {
            Arrays.fill(used, false);
            findAugmentPath(i);
        }

        for (int i = 0; i < pointRight.size(); i++) {
            if (match[i] != -1) {
                int clusterID = pointLeft.get(match[i]);
                Point currentPoint = pointList.get(pointRight.get(i));
                currentPoint.setClusterID(pointList.get(clusterID).getClusterID());
                int mustID = currentPoint.getMustID();

                if (mustID != -1) {
                    for (int linkedPointID : mustLinkSet.get(mustID)) {
                        pointList.get(linkedPointID).setClusterID(pointList.get(clusterID).getClusterID());
                    }
                }
            }


        }
    }
}