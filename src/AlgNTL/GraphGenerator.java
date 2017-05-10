package AlgNTL;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jakub on 10.05.2017.
 */
public class GraphGenerator {
    private static final Random rand = new Random();

    public Graph generateGraph(int vertexNumber, int edgeNumber) {
        if (edgeNumber - 1 < vertexNumber) {
            throw new IllegalArgumentException();
        }
        /*
        if (edgeNumber < maxDegree) {
            throw new IllegalArgumentException();
        }
        */
        Graph graph = new Graph();
        graph.addVertices(vertexNumber);
        List<Vertex> vertexs = new ArrayList<>(graph.getVertices());
        List<Edge> edges = new ArrayList<>(edgeNumber);


        //generowanie losowych polaczen
        int edgeId = generateRandomConnections(vertexNumber, vertexs, edges);
        /*
        int maxDegreeVertexes = getMaxDegreeCount(vertexs, maxDegree);
        if (maxDegreeVertexes == 0) {
            generateMaxDegreeVertexes(vertexNumber, maxDegree, edges, vertexs, edgeId);
        }
        */

        if (edges.size() > edgeNumber) {
            removeEdges(edges, vertexs, edgeNumber, 0);//maxDegree);
        } else if (edges.size() < edgeNumber) {
            addEdges(edges, vertexs, edgeNumber, edgeId, 0);//maxDegree);
        }


        for (Edge e : edges) {
            graph.addEdge(e.getV1Id(), e.getV2Id(), e.getId());
        }
        System.out.println("set :" + edgeNumber + "got :" + graph.getEdges().size());
        return graph;
    }

    private void addEdges(List<Edge> edges, List<Vertex> vertexs, int edgeNumber, int edgeId, int maxDegree) {
        List<VertexPair> possibleCombinations = new ArrayList<>(vertexs.size() * vertexs.size());
        for(int i = 0; i < vertexs.size(); i ++){
            for(int j = 0; j < vertexs.size(); j++){
                possibleCombinations.add(new VertexPair(vertexs.get(i),vertexs.get(j)));
            }
        }

        while (edges.size() != edgeNumber) {
            if(possibleCombinations.isEmpty()){
                throw  new IllegalArgumentException();
            }
            int index = rand.nextInt(possibleCombinations.size());
            VertexPair pair = possibleCombinations.get(index);
            if(canAddEdge(pair.getVertex1().getId(), pair.getVertex2().getId(), vertexs, edges)){
                edges.add(new Edge(pair.getVertex1(),pair.getVertex2(), edgeId ++));
            }
            possibleCombinations.remove(index);
        }
    }

    private void removeEdges(List<Edge> edges, List<Vertex> vertexs, int edgeNumber, int maxDegree) {
        List<Edge> possibleEdges = new ArrayList<>(edges);
        while (edges.size() != edgeNumber) {
            int index = rand.nextInt(edges.size());
            Edge edge = edges.get(index);
            if(canRemoveEdge(edge, vertexs, edges)){
                edges.remove(edge);
            }
            possibleEdges.remove(index);
        }
    }

    private void generateMaxDegreeVertexes(int vertexNumber, int maxDegree, List<Edge> edges, List<Vertex> vertexes, int edgeId) {
        int vertexesToGenerate = rand.nextInt(maxDegree) + 1;
        List<Vertex> vertexCopy = new ArrayList<>(vertexes);
        for (int i = 0; i < vertexesToGenerate; i++) {
            int index = rand.nextInt(vertexes.size());
            Vertex chosenVertex = vertexes.get(index);
            vertexCopy.remove(index);
            List<Vertex> possibleOtherVertexes = getPossibleNeighbours(chosenVertex, vertexes);
            for (int j = chosenVertex.getDegree(); j < maxDegree; j++) {
                int otherIndex = rand.nextInt(possibleOtherVertexes.size());
                edges.add(new Edge(chosenVertex, possibleOtherVertexes.get(otherIndex), edgeId++));
                possibleOtherVertexes.remove(otherIndex);
            }
            if (getMaxDegreeCount(vertexes, maxDegree) >= vertexesToGenerate) {
                return;
            }
        }
    }

    private List<Vertex> getPossibleNeighbours(Vertex vertex, List<Vertex> vertexes) {
        return vertexes
                .stream()
                .filter(f -> f.getId() != vertex.getId())
                .filter(f -> f.hasNeighbout(vertex))
                .collect(Collectors.toList());
    }

    private int getMaxDegreeCount(List<Vertex> vertexs, int maxDegree) {
        return (int) vertexs.stream().filter(f -> f.getDegree() == maxDegree).count();
    }

    private int generateRandomConnections(int vertexNumber, List<Vertex> vertexs, List<Edge> edges) {
        int edgeId = 0;
        for (int i = 0; i < vertexNumber; i++) {
            for (int j = 0; j < vertexNumber; j++) {
                if (rand.nextBoolean()) {
                    if (canAddEdge(i, j, vertexs, edges)) {
                        edges.add(new Edge(vertexs.get(i), vertexs.get(j), edgeId++));
                    }
                }
            }
        }
        return edgeId;
    }

    private boolean canAddEdge(int i, int j, List<Vertex> vertexes, List<Edge> edges) {
        int v1 = vertexes.get(i).getId();
        int v2 = vertexes.get(j).getId();
        if(v1 == v2)
            return false;

        int num =  (int)edges
                .stream()
                .filter(f -> (f.getV1Id() == v1 && f.getV2Id() == v2) || (f.getV1Id() == v2 && f.getV2Id() == v1))
                .count();
        return num == 0;
    }

    private  boolean canRemoveEdge(Edge edge, List<Vertex> vertexes, List<Edge> edges){
        return true;
    }
}

class VertexPair {
    private  Vertex vertex1;
    private  Vertex vertex2;

    public VertexPair(Vertex vertex1, Vertex vertex2) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    public Vertex getVertex1() {
        return vertex1;
    }

    public void setVertex1(Vertex vertex1) {
        this.vertex1 = vertex1;
    }

    public Vertex getVertex2() {
        return vertex2;
    }

    public void setVertex2(Vertex vertex2) {
        this.vertex2 = vertex2;
    }
}