package main;

import modgraf.view.Editor;
import modgraf.view.MenuBar;
import modgraf.view.Toolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

	protected JButton moveButton;

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
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.setSize(120, 500);

		JButton newGameButton = new JButton();
		newGameButton.setIcon(new ImageIcon("newGame.png"));
		newGameButton.addActionListener(myAlgorithm.new ActionStartGameListener());
		panel.add(newGameButton);

		moveButton = new JButton();
		moveButton.setIcon(new ImageIcon("move.png"));
		moveButton.addActionListener(myAlgorithm.new ActionMoveComputerListener());
		panel.add(moveButton);

		//Create the radio buttons.
		String firstOption = "Player vs Computer";
		JRadioButton firstButton = new JRadioButton(firstOption);
		firstButton.setActionCommand(firstOption);
		firstButton.setSelected(true);

		String secondOption = "Computer vs Computer";
		JRadioButton secondButton = new JRadioButton(secondOption);
		secondButton.setActionCommand(firstOption);

		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(firstButton);
		group.add(secondButton);
		panel.add(firstButton);
		panel.add(secondButton);

		//Register a listener for the radio buttons.
		firstButton.addActionListener(myAlgorithm.new ActionChangeModeListener(MyAlgorithm.MODE_PLAYER_COMPUTER));
		secondButton.addActionListener(myAlgorithm.new ActionChangeModeListener(MyAlgorithm.MODE_COMPUTER_COMPUTER));

		JLabel infoLabel = new JLabel("Game not started");
		infoLabel.setName("infoLabel");
//		panel.add(infoLabel);

		return panel;
	}
}
