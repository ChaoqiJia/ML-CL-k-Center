package Algorithm.Tools;


import java.util.ArrayList;

//using min-max method to judge whether cover j points for the constrained set
public class matching2 {
    public static ArrayList<Integer> usedCenter;  //
    Graph graph;   //need n*n graph to be adjacent matrix
    public int[] match;     //record the correspondent points. Supposing match[0]=1, that means the two points are matching
    boolean[][] line;
    boolean[] used;  //for each points of augmenting-path, recording to be queried

    public matching2(Graph graph, float R) {
        this.usedCenter = new ArrayList<Integer>();
        this.graph = graph;
        line = new boolean[graph.getVertexesLeft().size()][graph.getVertexesRight().size()];
        used = new boolean[graph.getVertexesRight().size()];
        match = new int[graph.getVertexesRight().size()];


        for (int i = 0; i < graph.getVertexesRight().size(); i++) {
            match[i] = -1;
        }
        for (int i = 0; i < graph.getVertexesLeft().size(); i++) {
            for (int j = 0; j < graph.getVertexesRight().size(); j++) {
                float dist = graph.getMatrix()[j][i];
                if (dist > -1 && dist <= R) {
                    line[i][j] = true;
                } else {
                    line[i][j] = false;
                }
            }
        }
        for (int k = 0; k < graph.getVertexesLeft().size(); k++) {
            for (int l = 0; l < graph.getVertexesRight().size(); l++) {
                used[l] = false;
            }
        }
    }

    //whether to find the augmenting-path.
    boolean findAugmentPath(int left) {
        for (int i = 0; i < graph.getVertexesRight().size(); i++) {
            if (left < line.length)
                if (!used[i] && line[left][i]) {
                    used[i] = true;
                    if (match[i] == -1 || this.findAugmentPath(match[i])) {
                        match[i] = left;
                        return true;
                    }
                }
        }
        return false;
    }


    public int searchcount() {
        int countM = 0;

        for (int i = 0; i < graph.getVertexesLeft().size(); i++) {
            clearUsed();
            for (int j = 0; j < match.length; j++) {
                if (match[j] != -1)
                    usedCenter.add(match[j]);
            }
            findAugmentPath(i);
        }

        for (int i = 0; i < match.length; i++) {
            if (match[i] != -1) {
                countM++;
            }
        }
        return countM;
    }

    public int assignCannot(boolean ifflag, ArrayList<ArrayList<Point>> mustLinkSet, Graph graph1) {
        int countM = 0;

        for (int i = 0; i < graph.getVertexesLeft().size(); i++) {
            clearUsed();
            for (int j = 0; j < match.length; j++) {
                if (match[j] != -1)
                    usedCenter.add(match[j]);
            }
            findAugmentPath(i);
        }

        for (int i = 0; i < graph.getVertexesRight().size(); i++) {


            if (ifflag) {
                if (graph.getVertexesRight().size() > i) {
                    if (match[i] != -1) {
                        countM++;
                        graph.getVertexesRight().get(i).setClusterID(graph.getVertexesLeft().get(match[i]).getClusterID());
                        if (graph.getVertexesRight().get(i).getMustID() != -1) {
                            for (int j = 0; j < mustLinkSet.get(graph.getVertexesRight().get(i).getMustID()).size(); j++) {
                                mustLinkSet.get(graph.getVertexesRight().get(i).getMustID()).get(j).setClusterID(graph.getVertexesLeft().get(match[i]).getClusterID());
                            }
                        }
                    }
                }
            } else {
                if (graph.getVertexesRight().size() > i) {
                    if (match[i] == -1) {
                        graph1.addVertexesLeft(graph.getVertexesRight().get(i));
                    }
                }
            }

        }
        return countM;
    }

    void clearUsed() {
        for (int i = 0; i < graph.getVertexesRight().size(); i++) {
            used[i] = false;
        }
        usedCenter.clear();
    }

}