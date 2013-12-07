package main;

import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import modgraf.algorithm.ModgrafAbstractAlgorithm;
import modgraf.view.Editor;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

/** Klasa rozwiązuje problem najkrótsza ścieżka.
 * @author Daniel Pogrebniak
 * @see ModgrafAbstractAlgorithm
 * @see BellmanFordShortestPath */
public class MyAlgorithm extends ModgrafAbstractAlgorithm
{

    public MyAlgorithm(Editor e)
    {
        super(e);
        refactorEditor();
    }

    @Override
    public String getName()
    {
        return "My individual algorithm";
    }

    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        if (editor.getGraphT() instanceof WeightedGraph)
            openParamsWindow();
        else
            JOptionPane.showMessageDialog(editor.getGraphComponent(), "warning-wrong-graph-type" + "alg-sp-graph-type",
                    "warning", JOptionPane.WARNING_MESSAGE);
    }
    @Override
    protected void findAndShowResult()
    {
        List<DefaultEdge> result = BellmanFordShortestPath.findPathBetween(editor.getGraphT(), startVertex, endVertex);
        if (result != null)
        {
            createTextResult(result);
            createGraphicalResult(result);
        }
        else
            JOptionPane.showMessageDialog(editor.getGraphComponent(), "message-no-solution", "information",
                    JOptionPane.INFORMATION_MESSAGE);
    }
    private void createGraphicalResult(List<DefaultEdge> result)
    {
        int width = 4;
        changeVertexStrokeWidth(startVertex, width);
        changeVertexStrokeWidth(endVertex, width);
        for (DefaultEdge edge : result)
            changeEdgeStrokeWidth(edge, width);
        editor.getGraphComponent().refresh();
    }

    private void createTextResult(List<DefaultEdge> result)
    {
        StringBuilder sb = new StringBuilder();
        mxGraphModel model = (mxGraphModel)editor.getGraphComponent().getGraph().getModel();
        Graph<String, DefaultEdge> graphT = editor.getGraphT();
        sb.append("alg-sp-message-1");
        sb.append((String)startVertexComboBox.getSelectedItem());
        sb.append("alg-sp-message-2");
        sb.append((String)endVertexComboBox.getSelectedItem());
        sb.append("alg-sp-message-3");
        if (result.size() == 1)
            sb.append("alg-sp-message-4");
        else
        {
            sb.append(result.size());
            if (result.size() > 1 && result.size() < 5)
                sb.append("alg-sp-message-5");
            if (result.size() > 4 )
                sb.append("alg-sp-message-6");
            sb.append("alg-sp-message-7");
            String start = (String)startVertexComboBox.getSelectedItem();
            sb.append(start);
            ArrayList<String> vertexIdList = new ArrayList<String>();
            vertexIdList.add(editor.getVertexId(start));
            for (DefaultEdge aResult : result) {
                sb.append(", ");
                String vertexId = graphT.getEdgeTarget(aResult);
                if (vertexIdList.contains(vertexId))
                    vertexId = graphT.getEdgeSource(aResult);
                vertexIdList.add(vertexId);
                mxCell vertex = (mxCell) model.getCell(vertexId);
                vertexIdList.add(vertex.getValue().toString());
                sb.append(vertex.getValue().toString());
            }
            sb.append(".");
        }
        editor.setText(sb.toString());
    }

    protected void refactorEditor() {
        Field frameField;
        try
        {
            frameField = Editor.class.getDeclaredField("frame");
            frameField.setAccessible(true);
            JFrame editorFrame = (JFrame) (frameField.get(editor));
            editorFrame.setName("hacked frame");
            editorFrame.setTitle("hacked frame");
        }
        catch (NoSuchFieldException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (SecurityException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (IllegalArgumentException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (IllegalAccessException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}


