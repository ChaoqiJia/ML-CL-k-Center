package Algorithm.Tools;

import java.util.ArrayList;
import java.util.List;

public class tools {

    // calculate the distance between the two points x,y
    public static float distance(Point x, Point y) {
        double dist = 0;
        for (int i = 0; i < x.getFeature().length; i++) {
            dist += (x.getFeature()[i] - y.getFeature()[i]) * (x.getFeature()[i] - y.getFeature()[i]);
        }
        return (float) Math.sqrt(dist);
    }


    public static float[] getRmax1(ArrayList<Point> pointList) {
        float maxDist = 0;
        float minDist = Integer.MAX_VALUE;

        for (int i = 0; i < pointList.size() - 1; i++) {
            for (int j = i + 1; j < pointList.size(); j++) {
                float linea = distance(pointList.get(i), pointList.get(j));
                maxDist = Math.max(maxDist, linea);
                minDist = Math.min(minDist, linea);
            }
        }
        float differDist = (float) (5 / Math.pow(10, 5));//set the diff
        return new float[]{maxDist, minDist, differDist};
    }


}