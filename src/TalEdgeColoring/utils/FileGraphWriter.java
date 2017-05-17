package TalEdgeColoring.utils;

import TalEdgeColoring.graph.ColoringResult;
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
    public void writeOutputFile(String name, ColoringResult result, Graph graph) {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString() + "\\" + name;
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
            for (Edge e : graph.getEdges()) {
                bw.write("EDGE\t" + e.getId() + "\t" + e.getV1Id() + "\t" + e.getV2Id() + "\t" + e.getColor() + "\n");
            }
            bw.write("DEGREE\t" + graph.getDegree() + "\n"); //dla porownania liczby kolorow do stopnia grafu
            if(result != null){
                bw.write("COL_NUM\t" + result.getChromaticIndex() + "\n");
                bw.write("TIME\t" + result.getTime() + "\n"); //nanosekund
                bw.write("MEM\t" + result.getMemory() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
