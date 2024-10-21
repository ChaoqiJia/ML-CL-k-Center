package addConstraints;

import Tools.Point;
import OurAlg.ourComputer;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static Tools.tools.getRmax1;


public class OutPut {

    // Declare point lists and variables
    static ArrayList<Point>  pointList = new ArrayList<>();
    static int k = , d = , markPosition = ; // k: number of clusters, d: dimension, markPosition: label position
    static float[] RMark;

    public static void main(String[] args) {
        int count = 40; //the number of cycles
        double[] c = {0.02,0.04,0.06,0.08,0.1}; //the ratio of the number of constraints to the number of points in the dataset
        
        // Iterate over different constraints
        for (double constraint : c) {
            // Input and output file names
            String inputFilename = "";
            String vertexFilename = "";

            // Initialize constrained points generator
            Constraints constraints = new Constraints();
            readMyFile(inputFilename); // Read input file

            // Compute maximal distance and minimal distance between any pairwise points in the pointList
            RMark = getRmax1(pointList);
//          System.out.println(RMark);

            // Iterate to run experiments
            for (int j = 0; j < count; j++) {
                constraints.constraints(constraint, pointList, k);

                // Run our algorithm
                ourComputer ourAlg = new ourComputer(pointList, constraints.cannotList, constraints.mustList, k, RMark);

                // Perform random experiments and store results
                for (int exp = 0; exp < count; exp++) {
                    float[] vertex = ourAlg.vertexCover();
                    output(vertexFilename, Arrays.toString(vertex) + "\n");
                }
            }
        }
    }


    public static void readMyFile(String filename) {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String str;
            int i = 0;

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

    // Method to output results to a file
    public static void output(String filename, String data) {
        File file = new File(filename);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs(); // Create parent directories if they don't exist
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
