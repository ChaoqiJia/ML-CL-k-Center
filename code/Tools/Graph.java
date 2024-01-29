package Algorithm.Tools;

import java.util.ArrayList;
import java.util.Arrays;

public class Graph {
    //vertexes are the points of database and the matrix is the distance between two points,;
    private ArrayList<Point> vertexesLeft;
    private ArrayList<Point> vertexesRight;
    private float matrix[][];

    //a constructor of the class, using to initialize the information
    public Graph(ArrayList<Point> vertexesRight, int k) {
        this.vertexesRight = vertexesRight;
        this.matrix = new float[3 * k][vertexesRight.size()];
    }


    public Graph(ArrayList<Point> vertexesLeft, ArrayList<Point> vertexesRight, ArrayList<ArrayList<Point>> mustLinkSet) {
        this.vertexesLeft = vertexesLeft;
        this.vertexesRight = vertexesRight;
        int range = Math.max(vertexesLeft.size(), vertexesRight.size());
        this.matrix = new float[range][range];
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                this.matrix[i][j] = Float.valueOf(-1);
            }
        }

        for (int i = 0; i < vertexesLeft.size(); i++) {
            for (int j = 0; j < vertexesRight.size(); j++) {
                this.matrix[j][i] = tools.distance(vertexesLeft.get(i), vertexesRight.get(j));
                if (vertexesRight.get(j).getMustID() != -1) {
                    Float maxDist = Float.valueOf(0);
                    for (int k = 0; k < mustLinkSet.get(vertexesRight.get(j).getMustID()).size(); k++) {
                        maxDist = Math.max(maxDist, tools.distance(vertexesLeft.get(i), mustLinkSet.get(vertexesRight.get(j).getMustID()).get(k)));
                    }
                    this.matrix[j][i] = maxDist;
                }

            }
        }

    }


    public ArrayList<Point> getVertexesLeft() {
        if (vertexesLeft == null) {
            this.vertexesLeft = new ArrayList<>();
        }
        return vertexesLeft;
    }

    public ArrayList<Point> getVertexesRight() {
        return vertexesRight;
    }

    public float[][] getMatrix() {
        return matrix;
    }

    @Override
    public String toString() {
        return "Graph{" + "vertexesLeft=" + Arrays.toString(vertexesLeft.toArray()) + ", vertexesRight=" + Arrays.toString(vertexesRight.toArray()) + ", matrix=" + Arrays.toString(matrix) + '}';
    }


    public void clear() {
        this.vertexesLeft = null;
        for (Point rightP : this.vertexesRight) {
            rightP.setClusterID(-1);
        }
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[i].length; j++) {
                this.matrix[i][j] = -1;
            }
        }
    }


    public void addVertexesLeft(Point point) {
        if (this.vertexesLeft == null) {
            this.vertexesLeft = new ArrayList<Point>();
        }

        point.setClusterID(this.vertexesLeft.size());
        this.vertexesLeft.add(point);
    }
}
