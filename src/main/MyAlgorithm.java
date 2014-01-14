package main;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import modgraf.algorithm.ModgrafAbstractAlgorithm;
import modgraf.view.Editor;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Klasa rozwiązuje problem najkrótsza ścieżka.
 *
 * @author Daniel Pogrebniak
 * @see ModgrafAbstractAlgorithm
 * @see BellmanFordShortestPath
 */
public class MyAlgorithm extends ModgrafAbstractAlgorithm implements mxEventSource.mxIEventListener
{

	public static String COLOR_DEFAULT = "c3d9ff";
	public static String COLOR_RED = "DB6F65";
	public static String COLOR_BLUE = "65D1DB";
	public static String COLOR_YELLOW = "F3EF71";

	protected Set<String> player1Vertices = new HashSet<String>();
	protected Set<String> enemyVertices = new HashSet<String>();

	protected String selectedVertex;

	public MyAlgorithm(Editor e)
	{
		super(e);
		editor.getGraphComponent().getGraphControl().addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				System.out.println("mouse released");
				if (!editor.getGraphComponent().getGraph().isSelectionEmpty())
				{
					String selectionId = ((mxCell) editor.getGraphComponent().getGraph().getSelectionCell()).getId();
					if (selectedVertex != selectionId)
					{
						System.out.println("new selection");
						movePlayer();
						selectedVertex = selectionId;
					}
				}
				else
				{
					selectedVertex = null;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}
		});
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
		Graph<String, DefaultEdge> graphCopy = new SimpleGraph(DefaultEdge.class);
		Graphs.addGraph(graphCopy, editor.getGraphT());

		//		List<DefaultEdge> result = BellmanFordShortestPath.findPathBetween(editor.getGraphT(), startVertex, endVertex);
		List<DefaultEdge> result = DijkstraShortestPath.findPathBetween(graphCopy, startVertex, endVertex);
		if (result != null)
		{
			createTextResult(result);
			createGraphicalResult(result);
		}
		else
			JOptionPane.showMessageDialog(editor.getGraphComponent(), "message-no-solution", "information",
			  JOptionPane.INFORMATION_MESSAGE);
	}

	protected Collection<String> findResultVertices()
	{
		Set<String> resultSet = new HashSet<String>();
		Graph<String, DefaultEdge> graphCopy = new SimpleGraph(DefaultEdge.class);
		Graphs.addGraph(graphCopy, editor.getGraphT());

		List<DefaultEdge> result = DijkstraShortestPath.findPathBetween(graphCopy, startVertex, endVertex);

		return resultSet;
	}


	private void createGraphicalResult(List<DefaultEdge> result)
	{
		int width = 4;
		//		changeVertexStrokeWidth(startVertex, width);
		//		changeVertexStrokeWidth(endVertex, width);
		for (DefaultEdge edge : result)
			changeEdgeStrokeWidth(edge, width);
		editor.getGraphComponent().refresh();
	}

	private void createTextResult(List<DefaultEdge> result)
	{
		StringBuilder sb = new StringBuilder();
		mxGraphModel model = (mxGraphModel) editor.getGraphComponent().getGraph().getModel();
		Graph<String, DefaultEdge> graphT = editor.getGraphT();
		sb.append("start item: ");
		sb.append((String) startVertexComboBox.getSelectedItem());
		sb.append("end item: ");
		sb.append((String) endVertexComboBox.getSelectedItem());
		sb.append("result count: ");
		sb.append(result.size());
		if (result.size() == 1)
		{

		}
		else
		{
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

	protected void clearAllEdges()
	{
		for (Object edge : editor.getGraphT().edgeSet())
		{
			changeEdgeStrokeWidth((DefaultEdge) edge, 1);
		}
	}

	protected void startGame()
	{
		System.out.println("Start game clicked");

		startVertex = endVertex = null;
		player1Vertices.clear();
		enemyVertices.clear();

		Object parent = editor.getGraphComponent().getGraph().getDefaultParent();
		Object[] childVertices = editor.getGraphComponent().getGraph().getChildVertices(parent);
		Object[] childEdges = editor.getGraphComponent().getGraph().getChildEdges(parent);
		System.out.println("Child vertices:" + childVertices.length);
		for (Object cell : childVertices)
		{
			colorVertex((mxCell) cell, COLOR_DEFAULT);
			changeVertexStrokeWidth(((mxCell) cell).getId(), 1);
		}
		clearAllEdges();
		markStartVertices();

		editor.getGraphComponent().getGraph().setSelectionCell(null);
	}

	protected void endGame()
	{
		JOptionPane.showMessageDialog(editor.getGraphComponent(), "All vertices selected",
		  "Game Over", JOptionPane.INFORMATION_MESSAGE);
		startVertex = endVertex = null;

		//find computer path
		Set<String> resultCells = findPath(false, true);
		if (!resultCells.isEmpty())
		{

		}

		//find user path
		resultCells = findPath(true, false);
		if (!resultCells.isEmpty())
		{

		}

	}

	protected boolean canMove()
	{
		if (startVertex == null || endVertex == null)
		{
			System.out.println("start and end vertices not selected");
			return false;
		}
		if (editor.getGraphComponent().getGraph().getSelectionCount() != 1)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(), "Select 1 vertex",
			  "Information", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		mxCell cell = (mxCell) editor.getGraphComponent().getGraph().getSelectionCell();
		if (!canSelectVertex(cell.getId()))
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(), "Can't select this vertex",
			  "Info", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	protected boolean canSelectVertex(String cellId)
	{
		if (player1Vertices.contains(cellId) || enemyVertices.contains(cellId) || startVertex.equals(cellId) || endVertex.equals(cellId))
		{
			return false;
		}
		return true;
	}

	protected boolean isEndGame()
	{
		int vertexCounter = editor.getVertexCounter();
		if (player1Vertices.size() + enemyVertices.size() + 2 >= vertexCounter)
		{
			return true;
		}
		return false;
	}


	protected Set<String> findPath(boolean includePlayerV, boolean includeEnemyV)
	{
		Graph<String, DefaultEdge> graphCopy = new SimpleGraph(DefaultEdge.class);
		Graphs.addGraph(graphCopy, editor.getGraphT());
		if (!includePlayerV)
		{
			for (String vertexId : player1Vertices)
			{
				graphCopy.removeVertex(vertexId);
			}
		}
		if (!includeEnemyV)
		{
			for (String vertexId : enemyVertices)
			{
				graphCopy.removeVertex(vertexId);
			}
		}

		//		List<DefaultEdge> result = BellmanFordShortestPath.findPathBetween(editor.getGraphT(), startVertex, endVertex);
		List<DefaultEdge> result = DijkstraShortestPath.findPathBetween(graphCopy, startVertex, endVertex);

		Set<String> resultSet = new HashSet<String>();
		if (result != null)
		{
			System.out.println("Result vertices: " + result.size());
			for (DefaultEdge edge : result)
			{
				String source = (String) editor.getGraphT().getEdgeSource(edge);
				String target = (String) editor.getGraphT().getEdgeTarget(edge);
				resultSet.add(source);
				resultSet.add(target);
			}
			if (resultSet.size()>2)
			{
				createGraphicalResult(result);
			}
//			selectVertex(resultSet);
		}
		else
		{
			System.out.println("Solution not found");
		}

		return resultSet;
	}

	protected Set<String> findAvailableVertices()
	{
		Object parent = editor.getGraphComponent().getGraph().getDefaultParent();
		Object[] childVertices = editor.getGraphComponent().getGraph().getChildVertices(parent);
		Set<String> availableVertices = new HashSet<String>();
		for (Object ver : childVertices)
		{
			String cellId = ((mxCell) ver).getId();
			if (canSelectVertex(cellId))
			{
				availableVertices.add(cellId);
			}
		}

		return availableVertices;
	}

	protected boolean moveEnemy()
	{
		//try to build path solution
		Set<String> resultCells = findPath(false, true);
		if (!resultCells.isEmpty())
		{
			selectVertex(resultCells);
			return true;
		}

		//block other player's path
		resultCells = findPath(true, false);
		if (!resultCells.isEmpty())
		{
			selectVertex(resultCells);
			return true;
		}

		//find any available vertex
		resultCells = findAvailableVertices();
		if (!resultCells.isEmpty())
		{
			selectVertex(resultCells);
			return true;
		}

		return false;
	}

	protected boolean selectVertex(Set<String> resultCells)
	{
		Iterator<String> iterator = resultCells.iterator();
		String selectedId = iterator.next();
		mxGraphModel model = (mxGraphModel) editor.getGraphComponent().getGraph().getModel();
		mxCell selectedCell = (mxCell) model.getCell(selectedId);
		colorVertex(selectedCell, COLOR_BLUE);
		enemyVertices.add(selectedId);
		return false;
	}

	protected boolean movePlayer()
	{
		if (!canMove())
		{
			System.out.println("Can't move");
			return false;
		}

		mxCell cell = (mxCell) editor.getGraphComponent().getGraph().getSelectionCell();

		//		editor.getGraphComponent().
		String cellId = cell.getId();
		colorVertex(cell, COLOR_RED);
		player1Vertices.add(cellId);

		if (isEndGame())
		{
			endGame();
			return true;
		}
		clearAllEdges();

		moveEnemy();

		if (isEndGame())
		{
			endGame();
			return true;
		}

		return true;

	}


    //      Checking who is the winner at the end of the game
    protected void checkWinner()
    {
        Graph<String, DefaultEdge> graphCopy = new SimpleGraph(DefaultEdge.class);
        Graphs.addGraph(graphCopy, editor.getGraphT());
        for (String vertexId : enemyVertices)
        {
            graphCopy.removeVertex(vertexId);
        }

        List<DefaultEdge> result = DijkstraShortestPath.findPathBetween(graphCopy, startVertex, endVertex);

        Set<String> resultSet = new HashSet<String>();
        if (result != null)
        {
            System.out.println("Result vertices: " + result.size());
            createGraphicalResult(result);
            for (DefaultEdge edge : result)
            {
                String source = (String) editor.getGraphT().getEdgeSource(edge);
                String target = (String) editor.getGraphT().getEdgeTarget(edge);
                if (canSelectVertex(source))
                {
                    resultSet.add(source);
                }
                if (canSelectVertex(target))
                {
                    resultSet.add(target);
                }
            }

            JOptionPane.showMessageDialog(editor.getGraphComponent(), "Human win",
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            JOptionPane.showMessageDialog(editor.getGraphComponent(), "Human has no connection",
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }

        graphCopy = new SimpleGraph(DefaultEdge.class);
        Graphs.addGraph(graphCopy, editor.getGraphT());
        for (String vertexId : player1Vertices)
        {
            graphCopy.removeVertex(vertexId);
        }

        result = DijkstraShortestPath.findPathBetween(graphCopy, startVertex, endVertex);
        resultSet = new HashSet<String>();

        if (result != null)
        {
            System.out.println("Result vertices: " + result.size());
            createGraphicalResult(result);
            for (DefaultEdge edge : result)
            {
                String source = (String) editor.getGraphT().getEdgeSource(edge);
                String target = (String) editor.getGraphT().getEdgeTarget(edge);
                if (canSelectVertex(source))
                {
                    resultSet.add(source);
                }
                if (canSelectVertex(target))
                {
                    resultSet.add(target);
                }
            }

            JOptionPane.showMessageDialog(editor.getGraphComponent(), "Enemy win",
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            JOptionPane.showMessageDialog(editor.getGraphComponent(), "Enemy has no connection",
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
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
			startVertex = ((mxCell) cells[0]).getId();
			endVertex = ((mxCell) cells[1]).getId();
			changeVertexStrokeWidth(startVertex, 4);
			changeVertexStrokeWidth(endVertex, 4);

			return true;
		}
	}

	protected void colorVertex(mxCell cell, String color)
	{
		mxGraphModel model = (mxGraphModel) this.editor.getGraphComponent().getGraph().getModel();
		model.beginUpdate();
		String style = cell.getStyle();
		cell.setStyle("vertexStyle;fillColor=#" + color);
		model.endUpdate();
		editor.getGraphComponent().refresh();
	}

	@Override
	public void invoke(Object o, mxEventObject mxEventObject)
	{
		System.out.println("event received");
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
			startGame();
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
			movePlayer();

		}
	}
}


