package AlgNTL.gui;

import javax.swing.*;

/**
 * Created by Jakub on 14.05.2017.
 */
public class GeneratorOptionsDialog {
    static GeneratorOptionsResponse ShowDialog() {
        JTextField vertexes = new JTextField(5);
        JTextField edges = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("wierzchołki :"));
        myPanel.add(vertexes);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("krawędzie :"));
        myPanel.add(edges);

        GeneratorOptionsResponse resp = null;
        boolean ok = false;

        while (!ok) {
            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Wprowadz liczbe wierzcholkow i krawedzi", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int v = Integer.parseInt(vertexes.getText());
                    int e = Integer.parseInt(edges.getText());
                    resp = new GeneratorOptionsResponse();
                    resp.setEdgeNumber(e);
                    resp.setVertexNumber(v);
                    ok = resp.isCorrect();
                } catch (Exception e) {
                    ok = false;
                    resp = null;
                }
            } else {
                ok = true;
            }
        }
        return resp;
    }
}

class GeneratorOptionsResponse {
    private int vertexNumber = -1;
    private int edgeNumber = -1;

    public boolean isCorrect() {
        return vertexNumber > 0 && edgeNumber > 0 && (edgeNumber > vertexNumber - 1);
    }

    public int getVertexNumber() {
        return vertexNumber;
    }

    public void setVertexNumber(int vertexNumber) {
        this.vertexNumber = vertexNumber;
    }

    public int getEdgeNumber() {
        return edgeNumber;
    }

    public void setEdgeNumber(int edgeNumber) {
        this.edgeNumber = edgeNumber;
    }
}