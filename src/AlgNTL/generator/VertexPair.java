package AlgNTL.generator;

import AlgNTL.graph.Vertex;

/**
 * Created by Jakub on 14.05.2017.
 */
class VertexPair {
    private Vertex vertex1;
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