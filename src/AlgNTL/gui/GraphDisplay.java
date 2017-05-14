package AlgNTL.gui;

import AlgNTL.graph.Edge;
import AlgNTL.graph.Graph;
import AlgNTL.graph.Vertex;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.List;

/*
Written with jgraphx library (https://github.com/jgraph/jgraphx)  under BSD 3 licence

Copyright (c) 2001-2014, JGraph Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the JGraph nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL JGRAPH BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

/**
 * Created by Jakub on 08.05.2017.
 */
public class GraphDisplay {

    public static mxGraphComponent GetGraphComponent(Graph myGraph){
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        ColorsMap colors = new ColorsMap();

        List<Object> vertexes = new ArrayList<>(myGraph.getVertices().size());
        List<Object> edges = new ArrayList<>(myGraph.getEdges().size());
        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
        layout.setUseInputOrigin(true);
        graph.getModel().beginUpdate();
        try
        {
            for(Vertex vert : myGraph.getVertices()){
                Object v1 = graph.insertVertex(parent, null, vert.getId(), 20, 20, 80, 30);
                vertexes.add(v1);

            }
            for(Edge edge : myGraph.getEdges()){
                Object e = graph.insertEdge(parent, null, edge.getId() + " (" + edge.getColor() + ")", vertexes.get(edge.getV1Id()) , vertexes.get(edge.getV2Id()),"startArrow=classic;strokeColor="+ colors.get(edge.getColor()));
                edges.add(e);
            }
            layout.execute(parent);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setCenterPage(true);
        graphComponent.setConnectable(false);
        return graphComponent;
    }
}
