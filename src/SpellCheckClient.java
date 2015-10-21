import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.FileNotFoundException;
import javax.swing.event.*;
import java.util.Scanner;
public class SpellCheckClient{
	private static SPanel suggestionsPanel;
	private static JFrame frame;
	private static JPanel panel;
	private static JTextArea textarea;
	private static SpellChecker fixer;
	public static void main(String[] args) throws FileNotFoundException{
		init();
	}
	private static void init() throws FileNotFoundException{
		frame = new JFrame("Spell Checker");
		panel = new JPanel();
		textarea = new JTextArea();
		fixer = new SpellChecker();
		panel.setLayout(new BorderLayout());
		panel.add(textarea, BorderLayout.CENTER);
		frame.setContentPane(panel);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		textarea.getDocument().addDocumentListener(new DocumentListener(){
			@Override 
			public void insertUpdate(DocumentEvent e){
				textChanged();
			}
			
			@Override 
			public void changedUpdate(DocumentEvent e){
				textChanged();
			}
			
			@Override 
			public void removeUpdate(DocumentEvent e){
				textChanged();
			}
		});
		suggestionsPanel = new SPanel();
	}
	public static void textChanged(){
		String temp = textarea.getText();
		Scanner text = new Scanner(temp);
		String lastWord = "";
		while(text.hasNext()) lastWord = text.next();
		if(suggestionsPanel!=null)
			panel.remove(suggestionsPanel);
		suggestionsPanel.setSuggestions(fixer.fix(lastWord, 2, 5));
		panel.add(suggestionsPanel, BorderLayout.SOUTH);
	}
	
	public static void replaceLastWord(String replacement){
		String temp = textarea.getText();
		int index = Math.max(temp.trim().lastIndexOf("\n"), Math.max(temp.trim().lastIndexOf(" "), temp.trim().lastIndexOf("\t")))+1;
		temp = temp.substring(0, index)+replacement;
		textarea.setText(temp);
	}
	static class SPanel extends JPanel{
		String[] suggestions;
		JButton[] buttons;
		public void setSuggestions(String[] suggestions){
			for(int i = 0;buttons!=null && i < buttons.length; i++){
				SPanel.this.remove(buttons[i]);
			}
			this.suggestions=suggestions;
			buttons = new JButton[suggestions.length];
			setLayout(new GridLayout(1, suggestions.length));
			for(int i = 0; i < buttons.length; i++){
				buttons[i] = new JButton(suggestions[i]);
				buttons[i].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						replaceLastWord(((JButton)(e.getSource())).getText());
					}
				});
				SPanel.this.add(buttons[i]);
			}
		}
	}
}