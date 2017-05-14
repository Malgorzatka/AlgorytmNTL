package TalEdgeColoring.utils;

import TalEdgeColoring.graph.Edge;
import TalEdgeColoring.graph.Graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Jakub on 14.05.2017.
 */
public class FileGraphWriter {
    public void writeOutputFile(String name, long time, Graph graph) {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString() + "\\" + name;
        //System.out.println(s);
        File file = new File(s);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            java.io.FileWriter fw = new java.io.FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("VNUMBER\t" + graph.getVerticesSize() + "\n");
            for (Edge e : graph.getEdges())
                bw.write("EDGE\t" + e.getId() + "\t" + e.getV1Id() + "\t" + e.getV2Id() + "\t" + e.getColor() + "\n");
            bw.write("COL_NUM" + "\t" + graph.getColorNum() + "\n");
            bw.write("DEGREE\t" + graph.getDegree() + "\n"); //dla porownania liczby kolorow do stopnia grafu
            bw.write("TIME\t" + time + "\n"); //nanosekund
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
