package Algorithm.addConstraints;

import Algorithm.Tools.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MakeConstrainedPoints {
    ArrayList<Point> pointList = new ArrayList<Point>(), ConstrainedPoints = new ArrayList<Point>();
    ArrayList<ArrayList<Point>> pointListGroup = new ArrayList<>(), mustList = new ArrayList<>(), cannotList = new ArrayList<>();
    int ListSize = 0;
    int d, markPosition, k;

    public void makeConstrainedPoints(double rate, double rateJoint) {
        initConduct();
        ListSize = pointList.size();
        Collections.shuffle(pointList);
        for (int i = 0; i < pointList.size(); i++) {
            pointList.get(i).setID(i);
        }
        k = OutPut.k;
        d = OutPut.d;
        markPosition = OutPut.markPosition;
        for (int i = 0; i < ListSize * rate; i++) {
            ConstrainedPoints.add(pointList.get(i));
        }

        int groupNum = 0;
        for (int i = 0; i < ConstrainedPoints.size(); ) {
            Random agroup = new Random();
            int agroupsize = 0;
            if (ConstrainedPoints.size() - i == 1) {
                pointListGroup.get(--groupNum).add(ConstrainedPoints.get(i));
                break;
            }
            do {
                agroupsize = agroup.nextInt(ConstrainedPoints.size() - i) + 1;
            } while (agroupsize < 2);

            while (pointListGroup.size() <= groupNum) {
                pointListGroup.add(new ArrayList<Point>());
            }
            for (int j = 0; j < agroupsize; j++) {
                pointListGroup.get(groupNum).add(ConstrainedPoints.get(i));
                i++;
            }
            groupNum++;
        }

        double sizenum = 0;
        ArrayList<Integer> aa = new ArrayList<>();
        for (int i = 0; i < pointListGroup.size(); i++) {
            aa.add(pointListGroup.get(i).size());
        }
        for (int i = 0; i < pointListGroup.size(); i++) {
            sizenum += aa.get(i);
            double addsize = 0;
            if (sizenum > 0) {
                addsize = sizenum * rateJoint;
                for (int j = 0; j < addsize; j++) {
                    Point p = pointListGroup.get(i).get(j);
                    pointListGroup.get((i + 1) % pointListGroup.size()).add(p);
                }
            }
            if (addsize != 0)
                sizenum -= addsize / rateJoint;
        }
        for (int i = 0; i < pointListGroup.size(); i++) {
            Collections.shuffle(pointListGroup.get(i));
        }
        int CLNum = 0, MLNum = 0;
        for (int i = 0; i < pointListGroup.size(); i++) {
            List<Integer> cL = new ArrayList<Integer>();
            List[] mLID = new List[k];
            for (int j = 0; j < pointListGroup.get(i).size(); j++) {
                if (!cL.contains(pointListGroup.get(i).get(j).getLabel() % k)) {
                    cL.add(pointListGroup.get(i).get(j).getLabel() % k);
                    if (cannotList.size() <= CLNum) {
                        cannotList.add(new ArrayList<Point>());
                    }
                    pointListGroup.get(i).get(j).setConID(CLNum);
                    cannotList.get(CLNum).add(pointListGroup.get(i).get(j));
                }
                if (mLID[pointListGroup.get(i).get(j).getLabel() % k] == null) {
                    mLID[pointListGroup.get(i).get(j).getLabel() % k] = new ArrayList();
                }
                mLID[pointListGroup.get(i).get(j).getLabel() % k].add(j);
            }
            if (cannotList.get(CLNum).size() < 2) {
                cannotList.get(CLNum).get(0).setConID(-1);
                cannotList.get(CLNum--).clear();
            }
            CLNum++;
            for (int j = 0; j < mLID.length; j++) {
                if (mLID[j] != null && mLID[j].size() > 1) {
                    for (int l = 0; l < mLID[j].size(); l++) {
                        if (mustList.size() <= MLNum) {
                            mustList.add(new ArrayList<>());
                        }
                        pointListGroup.get(i).get((int) mLID[j].get(l)).setMustID(MLNum);
                        mustList.get(MLNum).add(pointListGroup.get(i).get((int) mLID[j].get(l)));

                    }
                    MLNum++;
                }
            }
        }

    }


    private void initConduct() {

        pointListGroup.clear();
        ConstrainedPoints.clear();
        mustList.clear();
        cannotList.clear();
        for (int i = 0; i < pointList.size(); i++) {
            pointList.get(i).setConID(-1);
            pointList.get(i).setMustID(-1);
        }
    }


    public void readMyFile(String filename) {
        try {
            FileReader filein = new FileReader(filename);
            String str;
            int i = 0;
            BufferedReader in = new BufferedReader(filein);

            while ((str = in.readLine()) != null) {
                String[] data = str.split("[,]");
                int mark = (int) Double.parseDouble(data[markPosition].replaceAll("[\\[\\] ]", "").trim());
                float[] positionPoint = new float[d];

                // the feature of the node
                for (int j = 0; j < d; j++) {
                    positionPoint[j] = Float.parseFloat(data[j + (markPosition + 1) % (d + 1)].replaceAll("[\\[\\] ]", ""));
                }

                //point[ID, feature[], label, conID, mustID]
                Point point = new Point(i++, positionPoint, mark, -1, -1);
                pointList.add(point);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
