package Tools;

import java.util.ArrayList;
import static Tools.tools.distance;

public class Metrics {
    static float[][] assess = null;
    public static String[] desc = {"Purity: ", ", NMI: ", ", RI: ", ", cost: ", ", runtimes: "};


    public static float[] metrics(int N, ArrayList<Point> pointlist, ArrayList<Integer> centers, int k, String times) {
        assess = new float[k][k];
        float maxDist = 0;

        for (int i = 0; i < N; i++) {
            maxDist = Math.max(maxDist, distance(pointlist.get(i), pointlist.get(centers.get(pointlist.get(i).getClusterID()))));

            //label and assignment
            int Pmark = pointlist.get(i).getLabel() % k;
            int Pcluster = pointlist.get(i).getClusterID();
            assess[Pcluster][Pmark]++;
        }
        String result =  Purity(N) + ", " + NMI(N) + ", " + RI(N) + ", " + costFunction(maxDist) + ", " + times;
        String[] data = result.split("[,]");
        float[] turnDouble = new float[data.length];

        for (int j = 0; j < data.length; j++) {
            turnDouble[j] = Float.parseFloat(data[j].replaceAll("[\\s+]", ""));
        }

        System.out.println(toString(turnDouble, desc));
        return turnDouble;
    }


    public static String toString(float[] metric, String[] desc) {
        StringBuilder accuracy = new StringBuilder();

        for (int i = 0; i < metric.length; i++) {
            accuracy.append(desc[i]).append(metric[i]);
        }
        return accuracy + ",\n";
    }


    private static float Purity(int N) {
        float testacc = 0;
        for (float[] floats : assess) {
            float maxAssess = 0;
            for (float aFloat : floats) {
                maxAssess = Math.max(aFloat, maxAssess);
            }
            testacc += maxAssess;
        }
        return testacc / N;
    }


    private static float NMI(int N) {

        float[] assessI = new float[assess.length];
        float[] assessJ = new float[assess.length];
        float[][] p = new float[assess.length][assess.length];

        for (int i = 0; i < assess.length; i++) {
            for (int j = 0; j < assess[i].length; j++) {
                p[i][j] = assess[i][j] / N;
                assessI[i] += assess[i][j] / N;
                assessJ[i] += assess[j][i] / N;
            }
        }

        float IXY = 0, HX = 0, HY = 0;
        for (int i = 0; i < assess.length; i++) {
            for (int j = 0; j < assess[i].length; j++) {
                if (p[i][j] != 0)
                    IXY += p[i][j] * Math.log(p[i][j] / (assessI[i] * assessJ[j]));
            }
            if (assessI[i] != 0)
                HX -= assessI[i] * Math.log(assessI[i]);
            if (assessJ[i] != 0)
                HY -= assessJ[i] * Math.log(assessJ[i]);
        }
        return 2 * IXY / (HX + HY);
    }

    private static float RI(int N) {
        double testacc, com = 0;
        for (float[] floats : assess) {
            for (int j = 0; j < assess.length; j++) {
                com += (floats[j] * (floats[j] - 1) / 2.0);
            }
        }

        for (int i = 0; i < assess.length; i++) {
            for (int j = 0; j < assess.length; j++) {
                for (int l = i + 1; l < assess.length; l++) {
                    for (int m = 0; m < assess.length; m++) {
                        if (j != m) {
                            com += (assess[i][j] * assess[l][m]);
                        }
                    }
                }
            }

        }

        testacc = (com * 2.0 / ((N - 1) * 1.0 * N));
        System.out.println("RI: " + testacc);

        return (float) testacc;
    }


    private static float costFunction(float maxDist) {
        return maxDist;
    }

}