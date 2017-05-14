package AlgNTL.generator;

import AlgNTL.Edge;
import AlgNTL.Vertex;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Jakub on 14.05.2017.
 */
public class GraphTraverseChecker {
    public boolean canTraverseWithout(Vertex start, int vertexesNumber, int edgeToIgnore){
        HashSet<Integer> visitedIds = new HashSet<>();
        return canTraverseWithout(start, visitedIds, vertexesNumber, edgeToIgnore);
    }

    private boolean canTraverseWithout(Vertex start, HashSet<Integer> visitedIds, int vertexesNumber, int edgeToIgnore ){
        visitedIds.add(start.getId());
        boolean result = false;
        for (Edge edge : start.getIncidentEdges()){
            if(edge.getId() == edgeToIgnore){
                System.out.println("toIgnore " + edgeToIgnore +" start :" + start.getId() + " continued " + edge.getId());
                continue;
            }
            if(!visitedIds.contains(edge.getV1Id())){
                System.out.println("toIgnore " + edgeToIgnore +" start :" + start.getId() + " next " + edge.getV1Id());
                result = result || (visitedIds.size() == vertexesNumber) || canTraverseWithout(edge.getV1(), visitedIds, vertexesNumber, edgeToIgnore);
            }
            else if(!visitedIds.contains(edge.getV2Id())){
                System.out.println("toIgnore " + edgeToIgnore +" start :" + start.getId() + " next " + edge.getV2Id());
                result = result || (visitedIds.size() == vertexesNumber) || canTraverseWithout(edge.getV2(), visitedIds, vertexesNumber, edgeToIgnore);
            }
            else{
                System.out.println("toIgnore " + edgeToIgnore +" start :" + start.getId() + " skipped" + " " + edge.getV1Id() + " " + edge.getV2Id());
            }
        }
        return result || (visitedIds.size() == vertexesNumber);
    }
}
