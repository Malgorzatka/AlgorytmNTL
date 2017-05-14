package AlgNTL.generator;

import AlgNTL.graph.Edge;
import AlgNTL.graph.Vertex;

import java.util.HashSet;

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
                continue;
            }
            if(!visitedIds.contains(edge.getV1Id())){
                result = result || (visitedIds.size() == vertexesNumber) || canTraverseWithout(edge.getV1(), visitedIds, vertexesNumber, edgeToIgnore);
            }
            else if(!visitedIds.contains(edge.getV2Id())){
                result = result || (visitedIds.size() == vertexesNumber) || canTraverseWithout(edge.getV2(), visitedIds, vertexesNumber, edgeToIgnore);
            }
        }
        return result || (visitedIds.size() == vertexesNumber);
    }
}
