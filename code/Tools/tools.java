package Tools;

import java.util.ArrayList;

public class tools {

    // calculate the distance between the two points x,y
    public static float distance(Point A, Point B) {
        float[] vectorA = A.getFeature();
        float[] vectorB = B.getFeature();
        float sum = 0;
        for (int i = 0; i < vectorA.length; i++) {
            float diff = vectorA[i] - vectorB[i];
            sum += diff * diff;
        }

        return (float) Math.sqrt(sum);
    }

    // calculate the distance between the two points x,y
    private static float calculateEuclideanDistance(float[] vectorA, float[] vectorB) {
        float sum = 0;
        for (int i = 0; i < vectorA.length; i++) {
            float diff = vectorA[i] - vectorB[i];
            sum += diff * diff;
        }

        return (float) Math.sqrt(sum);
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
