package AlgNTL;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Malgorzata on 2017-04-17.
 */
public class MainFrame extends JFrame
{
    JMenu ntl;
    Graph graph;
    public MainFrame(String s)
    {
        super(s);
       // graph = null;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,200);
        setLocationRelativeTo(null);
        setMenu();
        setVisible(true);
    }

    private void setMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Plik");
        menuBar.add(menu);

        JMenuItem selectFile= new JMenuItem("Wybierz plik wejściowy");
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
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    readFile(file);
                }
            }
        });
        menu.add(selectFile);

        JMenuItem ntl = new JMenuItem("Algorytm NTL");
        ntl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long time = graph.colorNTL();//dodalam czas wykonywania algorytmu
                writeOutputFile("ntl_out.txt", time);//zmiana nazwy pliku wyjsciowego

            }
        });
        ntl.setEnabled(false);
        menu.add(ntl);



        setJMenuBar(menuBar);

    }

    private void writeOutputFile(String name, long time)
    {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString() + "\\" + name;
        //System.out.println(s);
        File file = new File(s);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("VNUMBER\t" + graph.getVerticesSize()+"\n" );
            for (Edge e : graph.getEdges())
                bw.write("EDGE\t" + e.getId() + "\t" + e.getV1Id() + "\t" + e.getV2Id() + "\t" + e.getColor() + "\n");
            bw.write("COL_NUM" + "\t" + graph.getColorNum() + "\n");
            bw.write("DEGREE\t" + graph.getDegree() + "\n"); //dla porownania liczby kolorow do stopnia grafu
            bw.write("TIME\t" + time + "\n"); //nanosekund
            bw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(File file)
    {
        try { //analizowanie pliku wejsciowego
            Scanner scanner = new Scanner(file);
            graph = new Graph();
            if(scanner.hasNext()) {
                if(scanner.next().equals("VNUMBER")) { //najpierw liczba wierzchlokow
                    int vnumber = scanner.nextInt();
                    graph.addVertices(vnumber);

                    // wypelniam macierz sasiedztwa zerami
                    // rozmiar macierzy vnumber x vnumber

                    for(int i=0; i<vnumber; i++)
                    {
                        ArrayList<Integer> temp = new ArrayList<Integer>();
                        for(int j=0; j<vnumber; j++)
                        {
                            temp.add(0);
                        }
                        graph.getNeighbourhoodMatrix().add(temp);
                    }

                    while(scanner.hasNext()) {
                        if(scanner.next().equals("EDGE")) { //wczytywanie krawedzi
                            int id = scanner.nextInt();
                            int idV1 = scanner.nextInt();
                            int idV2 = scanner.nextInt();
                            boolean tmp = false;
                            for (Edge e : graph.getEdges()) {
                                if((e.getV1().getId() == idV1 && e.getV2().getId() == idV2) || (e.getV1().getId() == idV2 && e.getV2().getId() == idV1)) {
                                    tmp = true;
                                }
                            }
                            if(idV1 != idV2 || tmp)
                                graph.addEdge(idV1, idV2, id);
                            else
                                System.out.println("To ma byc graf prosty");

                        }

                    }
                    ntl.setEnabled(true);
                }
                else  {
                    System.err.println("Bład przy parsowaniu pliku");
                }

            }
            scanner.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args)
    {
        MainFrame mf = new MainFrame("Edge Coloring");
    }
}
