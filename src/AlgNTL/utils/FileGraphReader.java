package AlgNTL.utils;

import AlgNTL.graph.Edge;
import AlgNTL.graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jakub on 14.05.2017.
 */
public class FileGraphReader {
    public Graph readGraph(File file) {
        try {
            //analizowanie pliku wejsciowego
            Scanner scanner = new Scanner(file);
            Graph graph = new Graph();
            if (scanner.hasNext()) {
                if (scanner.next().equals("VNUMBER")) { //najpierw liczba wierzchlokow
                    int vnumber = scanner.nextInt();
                    graph.addVertices(vnumber);

                    // wypelniam macierz sasiedztwa zerami
                    // rozmiar macierzy vnumber x vnumber

                    for (int i = 0; i < vnumber; i++) {
                        ArrayList<Integer> temp = new ArrayList<Integer>();
                        for (int j = 0; j < vnumber; j++) {
                            temp.add(0);
                        }
                        graph.getNeighbourhoodMatrix().add(temp);
                    }

                    while (scanner.hasNext()) {
                        if (scanner.next().equals("EDGE")) { //wczytywanie krawedzi
                            int id = scanner.nextInt();
                            int idV1 = scanner.nextInt();
                            int idV2 = scanner.nextInt();
                            boolean tmp = false;
                            for (Edge e : graph.getEdges()) {
                                if ((e.getV1().getId() == idV1 && e.getV2().getId() == idV2) || (e.getV1().getId() == idV2 && e.getV2().getId() == idV1)) {
                                    tmp = true;
                                }
                            }
                            if (idV1 != idV2 || tmp)
                                graph.addEdge(idV1, idV2, id);
                            else {
                                System.out.println("To ma byc graf prosty");
                                return null;
                            }
                        }

                    }
                } else {
                    System.err.println("Bład przy parsowaniu pliku");
                    return null;
                }
            }
            scanner.close();
            return graph;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
