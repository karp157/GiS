package main;

import com.mxgraph.swing.mxGraphComponent;
import modgraf.view.Editor;
import modgraf.view.MenuBar;
import modgraf.view.Toolbar;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * Created by Michal on 08.12.13.
 */
public class MyEditor extends Editor
{
	private MenuBar menuBar;
	private boolean modified;
	private JFrame frame;
	private Toolbar toolbar;

	public MyEditor(Editor e)
	{
		refactorEditor(e);
	}

	protected void refactorEditor(Editor edi)
	{
		try
		{
			Field frameField;
			Field toolbarField;
			Field menuField;
			Method addButtonMethod;

			frameField = Editor.class.getDeclaredField("frame");
			toolbarField = Editor.class.getDeclaredField("toolbar");
			menuField = Editor.class.getDeclaredField("menuBar");
			frameField.setAccessible(true);
			toolbarField.setAccessible(true);
			menuField.setAccessible(true);

			frame = (JFrame) (frameField.get(edi));
			toolbar = (Toolbar) (toolbarField.get(edi));
			menuBar = (MenuBar) (menuField.get(edi));

			frame.setTitle("hacked title");
			MyAlgorithm myAlgorithm = new MyAlgorithm(edi);
			menuBar.addAlgorithm("my own algorithm", myAlgorithm);
			toolbar.addSeparator();

			addButtonMethod = Toolbar.class.getDeclaredMethod("addButton", ActionListener.class, String.class);
			addButtonMethod.setAccessible(true);
			addButtonMethod.invoke(toolbar, myAlgorithm.new ActionStartGameListener(), "icons/add.png");
			addButtonMethod.invoke(toolbar, myAlgorithm.new ActionMakeMoveListener(), "icons/minus.png");

		}
		catch (NoSuchMethodException e0)
		{
			e0.printStackTrace();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
