package examples;
import javax.swing.*;
import java.awt.event.*;
import cartago.*;
import cartago.tools.*;

public class MySimpleGUI extends GUIArtifact {

	private MyFrame frame;
	
	public void setup() {
		frame = new MyFrame();
		
		linkActionEventToOp(frame.okButton,"ok");
		linkKeyStrokeToOp(frame.text,"ENTER","updateText");
		linkWindowClosingEventToOp(frame, "closed");

		defineObsProperty("value",getValue());
		frame.setVisible(true);		
	}

	@INTERNAL_OPERATION void ok(ActionEvent ev){
		signal("ok");
	}

	@INTERNAL_OPERATION void closed(WindowEvent ev){
		signal("closed");
	}
	
	@INTERNAL_OPERATION void updateText(ActionEvent ev){
		getObsProperty("value").updateValue(getValue());
	}

	@OPERATION void setValue(int value){
		frame.setText(""+value);
		getObsProperty("value").updateValue(getValue());
	}

	private int getValue(){
		return Integer.parseInt(frame.getText());
	}
	
	class MyFrame extends JFrame {		
		
		private JButton okButton;
		private JTextField text;
		
		public MyFrame(){
			setTitle("Simple GUI ");
			setSize(200,100);
			
			JPanel panel = new JPanel();
			setContentPane(panel);
			
			okButton = new JButton("ok");
			okButton.setSize(80,50);
			
			text = new JTextField(10);
			text.setText("0");
			text.setEditable(true);
			
			
			panel.add(text);
			panel.add(okButton);
			
		}
		
		public String getText(){
			return text.getText();
		}

		public void setText(String s){
			text.setText(s);
		}
	}
}
