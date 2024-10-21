package OurAlg;

import Tools.*;
import ilog.concert.*;
import ilog.cplex.*;

import java.util.ArrayList;
import java.util.List;

import static OurAlg.ourComputer.must;
import static OurAlg.ourComputer.pointList;
import static Tools.tools.distance;


public class RDS {

    private static int cv;
    private static int cu;
    private static List<Integer> thisCenter = new ArrayList<>(), thisCL = new ArrayList<>();


    public static void LP(int[][] X) {
        try {

            IloCplex cplex = new IloCplex();
            double[] lb = new double[cv];
            double[] ub = new double[cv];
            for (int i = 0; i < cv; i++) {
                lb[i] = 0.0;
                ub[i] = 1.0;
            }
            IloNumVar[] Z = cplex.numVarArray(cv, lb, ub);

            double[] lb1 = new double[cu];
            double[] ub1 = new double[cu];
            for (int i = 0; i < cu; i++) {
                lb1[i] = 0.0;
                ub1[i] = 1.0;
            }
            IloNumVar[] Y = cplex.numVarArray(cu, lb1, ub1);
            IloNumExpr expr = cplex.diff(cplex.sum(Y), cplex.sum(Z));
            for (int j = 0; j < cu; j++) {
                for (int i = 0; i < cv; i++) {
                    if (X[i][j] != 0) {
                        cplex.addLe(cplex.diff(Y[j], Z[i]), 0);
                    }
                }
            }
            cplex.addMaximize(expr);

            if (cplex.solve()) {
                if (cplex.getObjValue() != 0) {
                    double[] val = cplex.getValues(Y);
                    double[] val1 = cplex.getValues(Z);

                    for (int j = val1.length - 1; j >= 0; j--) {
                        if (val1[j] == 1) {
                            Point point = pointList.get(thisCenter.get(j));
                            int mustID = point.getMustID();
                            if (mustID != -1) {
                                must.remove(Integer.valueOf(mustID));
                            }
                            thisCenter.remove(j);
                        }
                    }
                    for (int j = 0; j < val.length; j++) {
                        if (val[j] == 1) {
                            int clIndex = thisCL.get(j);
                            int mustID = pointList.get(clIndex).getMustID();
                            thisCenter.add(clIndex);
                            if (mustID != -1){
                                must.add(mustID);
                            }
                        }
                    }
                }
            }
            cplex.clearModel();
            cplex.end();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public static void input(ArrayList<Point> pointList, List<Integer> center, List<Integer> CL, float R, ArrayList<ArrayList<Integer>> mustLinks, ArrayList<Integer> must) throws RuntimeException {
        thisCenter = center;
        thisCL = CL;

        cv = thisCenter.size();
        cu = thisCL.size();
        int[][] X = new int[cv][cu];

        for (int i = 0; i < cv; i++) {
            Point centerPoint = pointList.get(thisCenter.get(i));
            int centerMustID = centerPoint.getMustID();

            for (int j = 0; j < cu; j++) {
                Point clPoint = pointList.get(thisCL.get(j));
                int clMustID = clPoint.getMustID();

                float maxDist = 0;
                if (clMustID!= -1) {
                    for (int linkedPointID : mustLinks.get(clMustID)) {
                        float tempDist = distance(centerPoint, pointList.get(linkedPointID));
                        if (centerMustID != -1) {
                            for (int mustID : mustLinks.get(centerMustID)) {
                                tempDist = Math.max(tempDist, distance(pointList.get(linkedPointID), pointList.get(mustID)));
                            }
                        }
                        maxDist = Math.max(maxDist, tempDist);
                    }
                } else {
                    maxDist = distance(centerPoint, clPoint);
                    if (centerMustID != -1) {
                        for (int mustID : mustLinks.get(centerMustID)) {
                            maxDist = Math.max(maxDist, distance(clPoint, pointList.get(mustID)));
                        }
                    }
                }
                if (maxDist <= R) {
                    X[i][j] = 1;
                }
            }
        }
        LP(X);
    }


}
