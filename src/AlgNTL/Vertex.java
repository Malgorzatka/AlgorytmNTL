package AlgNTL;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Malgorzata on 2017-04-19.
 */
public class Vertex
{
    private ArrayList<Vertex> adjacencyList;
    private HashMap<Integer, Edge> incidentEdges;
    private HashMap<Integer, Boolean> missingColors;
    // z kazdym wierzcholkiem powinien byc skojarzony jeden kolor barkujacy
    // jest to potrzebne do tworzenia wachlarza
    private int missingColor;
    private int id;

    /**
     *
     * @param id
     */
    public Vertex(int id) {
        adjacencyList = new ArrayList<>();
        incidentEdges = new HashMap<>();
        missingColors = new HashMap<>();
        missingColor=1;
        this.id = id;
    }
    public boolean addNeigbour(Vertex v, Edge e) {
        incidentEdges.put(v.getId(), e);
        return adjacencyList.add(v) ;
    }

    public void removeNeigbout(Vertex v){
        incidentEdges.remove(v.getId());
        adjacencyList.remove(v);
    }
    /**
     *
     * @param neighbour
     * @return krawedz rozpoczynajaca sie w danym wierzcholku i konczaca sie w wierzchloku neighbour
     */
    public Edge getEdge (int neighbour) {
        return incidentEdges.get(neighbour);
    }
    public ArrayList<Vertex> getAdjacencyList() {
        return adjacencyList;
    }

    public int getDegree() {
        return adjacencyList.size();
    }

    public int getId() {
        return id;
    }

    public int getMissingColor() {
        return missingColor;
    }

    public void setMissingColor(int color) {
        this.missingColor=color;
    }

    public void removeMissingColor(int color) {
        missingColors.put(color, true);
    }

    public void addMissingColor(int color) {
        missingColors.remove(color);
    }

    public Boolean missingColor(int color) {
        if (missingColors.get(color) == null)
            return true;
        return false;
    }

    public void setActualMissingColor(int degree)
    {
//0		System.out.println("Brakujacy wierzch "+id +" to "+missingColor);
        int colorTmp = 1;
        while(!missingColor(colorTmp))
        {
            colorTmp++;
            // pytanie czy kolory numerujemy od zera??
            if (colorTmp == degree+2) colorTmp=1;
        }
        setMissingColor(colorTmp);
//0		System.out.println("Brakujacy wierzch "+id+" to "+missingColor);
    }

    public void setNextMissingColor (int degree, int col) {
        int colorTmp = col;
        while(!missingColor(colorTmp))
        {
            colorTmp++;
            // pytanie czy kolory numerujemy od zera??
            if (colorTmp == degree+2) colorTmp=1;
        }
        setMissingColor(colorTmp);
    }

    public Collection<Edge> getIncidentEdges(){
        return incidentEdges.values();
    }

    public boolean hasNeighbout(Vertex vertex){
        if(vertex == null){
            return false;
        }

        for(Edge edge : incidentEdges.values()){
            if(edge.getV1Id() == vertex.getId() || edge.getV2Id() == vertex.getId()){
                return  true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (missingColor != vertex.missingColor) return false;
        if (id != vertex.id) return false;
        if (adjacencyList != null ? !adjacencyList.equals(vertex.adjacencyList) : vertex.adjacencyList != null)
            return false;
        if (incidentEdges != null ? !incidentEdges.equals(vertex.incidentEdges) : vertex.incidentEdges != null)
            return false;
        return missingColors != null ? missingColors.equals(vertex.missingColors) : vertex.missingColors == null;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (missingColors != null ? missingColors.hashCode() : 0);
        result = 31 * result + missingColor;
        result = 31 * result + id;
        return result;
    }
}
