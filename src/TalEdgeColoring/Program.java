package TalEdgeColoring;

import TalEdgeColoring.generator.GraphGenerator;
import TalEdgeColoring.graph.ColoringResult;
import TalEdgeColoring.graph.Graph;
import TalEdgeColoring.gui.MainFrame;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jakub on 14.05.2017.
 */
public class Program {
    public static void main(String[] args) {
        //TODO dodac uruchamianie bez gui po parmaterach z konsoli
        try {
            if (args.length > 2) {
                String mode = args[0];
                int vertexes = Integer.parseInt(args[1]);
                int edges = Integer.parseInt(args[2]);
                String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".txt";
                if(args.length == 4){
                    fileName = args[3];
                }
                Graph graph = new GraphGenerator().generateGraph(vertexes,edges);
                if(mode.equals("opt")){
                    ColoringResult result = graph.optimalColor();
                    String text = "opt;" + vertexes + ";" + edges + ";" + graph.getDegree() + ";" + result.toString();
                    Files.write(Paths.get(fileName), text.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                }
                return;
            }
        } catch (Exception e) {
            int i = 5;
        }
        MainFrame mf = new MainFrame("Edge Coloring");
    }
}
