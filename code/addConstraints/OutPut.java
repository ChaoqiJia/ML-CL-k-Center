package Algorithm.addConstraints;

import Algorithm.GreedyAlg.greedyCompute;
import Algorithm.MatchingAlg.matchingCompute;
import Algorithm.Tools.Point;
import Algorithm.OurAlg.ourComputer;

import static Algorithm.Tools.tools.getRmax1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class OutPut {

    static ArrayList<Point> pointArrayList = new ArrayList<Point>();
    static int k = , d = , markPosition = ; //k: the number of clusters; d: dimension; markPosition: the position for the label
    static float[] RMark;

    public static void main(String[] args) {
        int count = 50;
        double[] c = {};
        double[] d = {};

        for (int i = 0; i < d.length; i++) {

            //input the file names, which output the results for each algorithm to different files
            String infilename = "";
            String matchingfilename = "";
            String greedyfilename = "";
            String vertexfilename = "";


            MakeConstrainedPoints makeConstrainedPoints1 = new MakeConstrainedPoints();
            makeConstrainedPoints1.k = k;
            makeConstrainedPoints1.readMyFile(infilename);
            pointArrayList = makeConstrainedPoints1.pointList;
            RMark = getRmax1(pointArrayList);


            for (int j = 0; j < count; j++) {

                makeConstrainedPoints1.makeConstrainedPoints(c[0], d[i]);

                //run our algorithm
                ourComputer ourAlg = new ourComputer(makeConstrainedPoints1.pointList, makeConstrainedPoints1.cannotList, makeConstrainedPoints1.mustList, k, RMark);

                //run Matching algorithm
                matchingCompute Matching = new matchingCompute();
                Matching.pointList = makeConstrainedPoints1.pointList;
                Matching.CannotList = makeConstrainedPoints1.cannotList;
                Matching.MustList = makeConstrainedPoints1.mustList;
                Matching.k = makeConstrainedPoints1.k;

                //run Greedy algorithm
                greedyCompute Greedy = new greedyCompute();
                Greedy.pointList = makeConstrainedPoints1.pointList;
                Greedy.CannotList = makeConstrainedPoints1.cannotList;
                Greedy.MustList = makeConstrainedPoints1.mustList;
                Greedy.k = makeConstrainedPoints1.k;


                for (int k = 0; k < count; k++) {
                    Random random = new Random();
                    int randomC = random.nextInt(makeConstrainedPoints1.ListSize);
                    float[] matching = Matching.matching(randomC, RMark);
                    float[] greedy = Greedy.greedy(randomC);
                    float[] vertex = ourAlg.vertexCover();

                    //output each result
                    output(matchingfilename, Arrays.toString(matching) + "\n");
                    output(greedyfilename, Arrays.toString(greedy) + "\n");
                    output(vertexfilename, Arrays.toString(vertex) + "\n");
                }


            }
        }
    }

    public static void output(String myFilename, String plainEffect) {
        File fileout = new File(myFilename);
        if (!fileout.getParentFile().exists()) {
            fileout.getParentFile().mkdirs();
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(myFilename, true))) {
            bufferedWriter.write(plainEffect);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
