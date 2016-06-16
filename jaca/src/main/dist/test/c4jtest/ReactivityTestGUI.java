package c4jtest;
import javax.swing.*;
import java.awt.event.*;
import cartago.*;
import cartago.tools.*;

public class ReactivityTestGUI extends GUIArtifact {

	private MyFrame frame;
	
	public void setup() {
		frame = new MyFrame();
		linkActionEventToOp(frame.testButton,"pressed");
		frame.setVisible(true);		
	}

	@INTERNAL_OPERATION void pressed(ActionEvent ev){
		signal("pressed");
	}

	class MyFrame extends JFrame {		
		
		private JButton testButton;
		
		public MyFrame(){
			setTitle("Reactivity Test ");
			setSize(200,100);
			
			JPanel panel = new JPanel();
			setContentPane(panel);
			
			testButton = new JButton("test");
			testButton.setSize(80,50);
			
			panel.add(testButton);
			
		}
		
	}
}
