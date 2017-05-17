package TalEdgeColoring;

import TalEdgeColoring.generator.GraphGenerator;
import TalEdgeColoring.graph.ColoringResult;
import TalEdgeColoring.graph.Graph;
import TalEdgeColoring.gui.MainFrame;
import TalEdgeColoring.utils.FileGraphWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Jakub on 14.05.2017.
 */
public class Program {
    public static void main(String[] args) {
        String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())  + ".txt";
        UUID guid = null;
        int vertexes = -1;
        int edges = -1;
        try {
            if (args.length > 2) {
                String mode = args[0];
                vertexes = Integer.parseInt(args[1]);
                edges = Integer.parseInt(args[2]);

                if(args.length == 4){
                    fileName = args[3];
                }
                Graph graph = new GraphGenerator().generateGraph(vertexes,edges);
                guid = UUID.randomUUID();
                String info = vertexes + ";" + edges + ";" + graph.getDegree() + ";" + guid.toString() + ";";
                saveGraph(graph,guid);
                if(mode.equals("opt")){
                    saveOptimalResult(info + "opt;", fileName, graph, true);
                }
                else if(mode.equals("ntl")){
                    saveNTLResult(info + "ntl;", fileName, graph, true);
                }
                else if(mode.equals("compare")){
                    saveOptimalResult(info + "opt;", fileName, graph, false);
                    graph = graph.getGraphUncoloredClone();
                    saveNTLResult(";ntl;", fileName, graph, true);
                }
                else{
                    System.out.println("zle parametry wejsciowe wpisz opt/ntl/comapre liczbaWierzcholkuw liczbaKrawedzi");
                }
                return;
            }
        } catch (Exception e) {
            try {
                Files.write(Paths.get(fileName + ".log"), ("ERROR: v=" + vertexes + " e=" + edges
                        + " graph: " + (guid == null ? "NULL" : guid.toString())
                        + " exception: " + e.getMessage()).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            }
            catch (Exception ex){}
            return;
        }
        MainFrame mf = new MainFrame("Edge Coloring");
    }

    private static void saveGraph(Graph graph, UUID guid){
        new File("GeneratedGraphs/").mkdirs();
        new FileGraphWriter().writeOutputFile("GeneratedGraphs/" + guid.toString(), null, graph);
    }

    private static void saveOptimalResult(String intro, String fileName, Graph graph, boolean endLine) throws IOException {
        ColoringResult result = graph.optimalColor();
        String text = intro + result.toString() + (endLine ? System.getProperty("line.separator") : "");
        Files.write(Paths.get(fileName), text.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    private static void saveNTLResult(String intro, String fileName, Graph graph, boolean endLine) throws IOException {
        ColoringResult result = graph.colorNTL();
        String text = intro  + result.toString() + (endLine ? System.getProperty("line.separator") : "");
        Files.write(Paths.get(fileName), text.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
}
