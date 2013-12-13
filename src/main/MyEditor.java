package main;

import modgraf.view.Editor;
import modgraf.view.MenuBar;
import modgraf.view.Toolbar;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

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
			frameField = Editor.class.getDeclaredField("frame");
			frameField.setAccessible(true);
			frame = (JFrame) (frameField.get(edi));
			MyAlgorithm myAlgorithm = new MyAlgorithm(edi);

			frame.add(BorderLayout.EAST, createPanel(myAlgorithm));

//			frame.setSize(1400, 750);
			frame.setBounds(100,100,1400,750);

		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	protected JPanel createPanel(MyAlgorithm myAlgorithm)
	{
		JPanel panel = new JPanel();
		panel.setSize(120, 500);
		JButton newGameButton = new JButton();
		newGameButton.setBounds(10, 10, 100, 30);
		newGameButton.setIcon(new ImageIcon("newGame.png"));
		newGameButton.addActionListener(myAlgorithm.new ActionStartGameListener());
		panel.add(newGameButton);

		JLabel infoLabel = new JLabel("Game not started");
		infoLabel.setName("infoLabel");
//		panel.add(infoLabel);

		return panel;
	}
}
