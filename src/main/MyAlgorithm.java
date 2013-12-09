package main;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import modgraf.algorithm.ModgrafAbstractAlgorithm;
import modgraf.view.Editor;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa rozwiązuje problem najkrótsza ścieżka.
 *
 * @author Daniel Pogrebniak
 * @see ModgrafAbstractAlgorithm
 * @see BellmanFordShortestPath
 */
public class MyAlgorithm extends ModgrafAbstractAlgorithm
{

	public static String COLOR_RED = "DB6F65";
	public static String COLOR_BLUE = "65D1DB";
	public static String COLOR_YELLOW = "F3EF71";

	public MyAlgorithm(Editor e)
	{
		super(e);
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
		mxGraphModel model = (mxGraphModel) editor.getGraphComponent().getGraph().getModel();
		Graph<String, DefaultEdge> graphT = editor.getGraphT();
		sb.append("alg-sp-message-1");
		sb.append((String) startVertexComboBox.getSelectedItem());
		sb.append("alg-sp-message-2");
		sb.append((String) endVertexComboBox.getSelectedItem());
		sb.append("alg-sp-message-3");
		if (result.size() == 1)
			sb.append("alg-sp-message-4");
		else
		{
			sb.append(result.size());
			if (result.size() > 1 && result.size() < 5)
				sb.append("alg-sp-message-5");
			if (result.size() > 4)
				sb.append("alg-sp-message-6");
			sb.append("alg-sp-message-7");
			String start = (String) startVertexComboBox.getSelectedItem();
			sb.append(start);
			ArrayList<String> vertexIdList = new ArrayList<String>();
			vertexIdList.add(editor.getVertexId(start));
			for (DefaultEdge aResult : result)
			{
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

	protected boolean move()
	{
		if (editor.getGraphComponent().getGraph().getSelectionCount() != 1)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(), "Select 1 vertex",
			  "Information", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else
		{
			mxCell cell = (mxCell) editor.getGraphComponent().getGraph().getSelectionCell();
			colorVertex((mxCell) cell, COLOR_RED);
			return true;
		}
	}

	protected boolean markStartVertices()
	{
		if (editor.getGraphComponent().getGraph().getSelectionCount() != 2)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(), "Select 2 start vertices",
			  "Information", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else
		{
			Object cells[] = editor.getGraphComponent().getGraph().getSelectionCells();
			for (Object cell : cells)
			{
				colorVertex((mxCell) cell, COLOR_YELLOW);
			}
			return true;
		}
	}

	protected void colorVertex(mxCell cell, String color)
	{
		mxGraphModel model = (mxGraphModel) this.editor.getGraphComponent().getGraph().getModel();
		model.beginUpdate();
		cell.setStyle("vertexStyle;fillColor=#" + color);
		model.endUpdate();
		editor.getGraphComponent().refresh();
	}

	class ActionStartGameListener implements ActionListener
	{
		ActionStartGameListener()
		{
			System.out.println("Start listener created");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Start game clicked");
			if (editor.getGraphComponent().getGraph().getSelectionCount() != 2)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(), "Select 2 start vertices",
				  "Information", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				markStartVertices();
			}
		}
	}

	class ActionMakeMoveListener implements ActionListener
	{
		ActionMakeMoveListener()
		{
			System.out.println("Start listener created");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Make move clicked");
			move();

		}
	}
}


