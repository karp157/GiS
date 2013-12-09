package main;

import modgraf.action.ActionNewGraph;
import modgraf.view.Editor;

import javax.swing.*;

/**
 * Created by Michal on 04.12.13.
 */
public class MainClass
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			JOptionPane.showMessageDialog(null,
			  "Nie udało się ustawić systemowego stylu okien!\nZostał użyty styl domyślny",

			  "Ostrzeżenie", 2);
		}
		Editor editor = new Editor();
		editor.createFrame();
		MyEditor myEditor = new MyEditor(editor);
		new ActionNewGraph(myEditor).actionPerformed(null);
	}
}