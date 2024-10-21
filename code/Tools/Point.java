package Tools;

import java.util.Arrays;

//point[id, feature[], label, conID, mustID]
public class Point {
    private int ID;
    private final float[] feature;
    private final int label;
    private int conID;
    private int mustID;

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

    private int clusterID;

    @Override
    public String toString() {
        return "Point{" +
                "ID=" + ID +
                ", feature=" + Arrays.toString(feature) +
                ", label=" + label +
                ", conID=" + conID +
                ", mustID=" + mustID +
                ", clusterID=" + clusterID +
                '}';
    }


    public Point(int ID, float[] feature, int label, int conID, int mustID) {
        this.ID = ID;
        this.feature = feature;
        this.label = label;
        this.conID = conID;
        this.mustID = mustID;
    }


    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() { return ID; }

    public float[] getFeature() {
        return feature;
    }

    public int getLabel() {
        return label;
    }

    public int getConID() {
        return conID;
    }

    public void setConID(int conID) {
        this.conID = conID;
    }

    public int getMustID() {
        return mustID;
    }

    public void setMustID(int mustID) {
        this.mustID = mustID;
    }
}