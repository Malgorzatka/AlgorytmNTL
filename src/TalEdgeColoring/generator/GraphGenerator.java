package TalEdgeColoring.generator;

import TalEdgeColoring.graph.Edge;
import TalEdgeColoring.graph.Graph;
import TalEdgeColoring.graph.Vertex;

import java.util.*;

/**
 * Created by Jakub on 10.05.2017.
 */
public class GraphGenerator {
    private static final Random rand = new Random();

    public Graph generateGraph(int vertexNumber, int edgeNumber) {
        if (edgeNumber < vertexNumber - 1) {
            throw new IllegalArgumentException();
        }

        Graph graph = new Graph();
        graph.addVertices(vertexNumber);
        List<Vertex> vertexes = new ArrayList<>(graph.getVertices());
        List<Edge> edges = new ArrayList<>(edgeNumber);

        //polaczenie wszsytkich mozliwych wierzcholkow ze soba
        createAllConnections(vertexNumber, vertexes, edges);

        if (edges.size() > edgeNumber) {
            removeEdges(edges, vertexes, edgeNumber);
        }

        //usniecie tymczasowych danych dodanych podczas generowania grafu
        for (Vertex v : vertexes) {
            v.getIncidentEdges().clear();
            v.getAdjacencyList().clear();
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            graph.addEdge(e.getV1Id(), e.getV2Id(), i);
        }
        for (int i = 0; i < vertexNumber; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for (int j = 0; j < vertexNumber; j++) {
                temp.add(0);
            }
            graph.getNeighbourhoodMatrix().add(temp);
        }
        return graph;
    }

    private void createAllConnections(int vertexNumber, List<Vertex> vertexs, List<Edge> edges) {
        int edgeId = 0;
        for (int i = 0; i < vertexNumber; i++) {
            for (int j = 0; j < vertexNumber; j++) {
                if (i != j && canAddEdge(i, j, vertexs, edges)) {
                    Vertex v1 = vertexs.get(i);
                    Vertex v2 = vertexs.get(j);
                    Edge e = new Edge(v1, v2, edgeId++);
                    edges.add(e);
                    v1.addNeigbour(v2, e);
                    v2.addNeigbour(v1, e);
                }
            }
        }
    }

    private boolean canAddEdge(int i, int j, List<Vertex> vertexes, List<Edge> edges) {
        int v1 = vertexes.get(i).getId();
        int v2 = vertexes.get(j).getId();
        if (v1 == v2)
            return false;

        int num = (int) edges
                .stream()
                .filter(f -> (f.getV1Id() == v1 && f.getV2Id() == v2) || (f.getV1Id() == v2 && f.getV2Id() == v1))
                .count();
        return num == 0;
    }

    private void removeEdges(List<Edge> edges, List<Vertex> vertexs, int edgeNumber) {
        List<Edge> possibleEdges = new ArrayList<>(edges);
        while (edges.size() != edgeNumber) {
            int index = rand.nextInt(possibleEdges.size());
            Edge edge = possibleEdges.get(index);
            if (canRemoveEdge(edge, vertexs)) {
                edges.remove(edge);
                Vertex v1 = edge.getV1();
                Vertex v2 = edge.getV2();
                v1.removeNeigbout(v2);
                v2.removeNeigbout(v1);
            }
            possibleEdges.remove(index);
        }
    }


    private boolean canRemoveEdge(Edge edge, List<Vertex> vertexes) {
        return new GraphTraverseChecker().canTraverseWithout(edge.getV1(), vertexes.size(), edge.getId());
    }
}

