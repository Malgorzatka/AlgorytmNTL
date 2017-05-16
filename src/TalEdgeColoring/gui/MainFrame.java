package TalEdgeColoring.gui;

import TalEdgeColoring.graph.Graph;
import TalEdgeColoring.generator.GraphGenerator;
import TalEdgeColoring.utils.FileGraphReader;
import TalEdgeColoring.utils.FileGraphWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Malgorzata on 2017-04-17.
 */
public class MainFrame extends JFrame {
    JMenuItem ntl;
    JMenuItem optimal;
    Graph graph;
    Component graphDisplay;

    public MainFrame(String s) {
        super(s);
        // graph = null;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMenu();
        setVisible(true);
    }

    private void setMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Plik");
        menuBar.add(menu);

        JMenuItem selectFile = new JMenuItem("Wybierz plik wejściowy");
        selectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Wybierz plik wejściowy");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                Path currentRelativePath = Paths.get("");
                String s = currentRelativePath.toAbsolutePath().toString();

                chooser.setCurrentDirectory(new File(s));
                int returnVal = chooser.showOpenDialog(chooser);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    readFile(file);
                }
            }
        });
        menu.add(selectFile);
        MainFrame self = this;
        ntl = new JMenuItem("Algorytm NTL");
        ntl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long time = graph.colorNTL();//dodalam czas wykonywania algorytmu
                writeOutputFile("ntl_out.txt", time);//zmiana nazwy pliku wyjsciowego
                self.updateGraph();
            }
        });
        ntl.setEnabled(false);
        menu.add(ntl);

        optimal = new JMenuItem("Algorytm optymalny");
        optimal.addActionListener(e -> {
            long time = graph.optimalColor().getTime();
            writeOutputFile("optimal.txt", time);
            self.updateGraph();
        });

        optimal.setEnabled(false);
        menu.add(optimal);

        JMenuItem generator = new JMenuItem("Generator");
        generator.addActionListener(e -> {
            GeneratorOptionsResponse response =  GeneratorOptionsDialog.ShowDialog();
            if(response != null){
                self.updateGraph(new GraphGenerator().generateGraph(response.getVertexNumber(), response.getEdgeNumber()));
                ntl.setEnabled(true);
                optimal.setEnabled(true);
            }
        });
        generator.setEnabled(true);
        menu.add(generator);

        setJMenuBar(menuBar);
    }

    public void updateGraph() {
        updateGraph(graph);
    }

    public void updateGraph(Graph graph) {
        if (graphDisplay != null)
            remove(graphDisplay);
        graphDisplay = GraphDisplay.GetGraphComponent(graph);
        add(graphDisplay);
        this.graph = graph;
        invalidate();
        validate();
        repaint();
    }

    private void writeOutputFile(String name, long time) {
        new FileGraphWriter().writeOutputFile(name,time,graph);
    }

    private void readFile(File file) {
        Graph graph = new FileGraphReader().readGraph(file);
        if(graph != null){
            updateGraph(graph);
            ntl.setEnabled(true);
            optimal.setEnabled(true);
        }
    }
}
