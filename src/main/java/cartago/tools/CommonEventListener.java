package cartago.tools;

import cartago.*;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class CommonEventListener implements IBlockingCmd, ActionListener, ItemListener, ListSelectionListener, WindowListener, MouseListener, MouseMotionListener, ChangeListener  {

	private BlockingQueue<EventOpInfo> events;
	private EventOpInfo fetched;
	
	public CommonEventListener(){
		events = new java.util.concurrent.ArrayBlockingQueue<EventOpInfo>(100);
		fetched = null;
	}
	
	public void exec(){
		try {
			fetched = events.take();
		} catch (Exception ex){
		}
	}
	
	public EventOpInfo getCurrentEventFetched(){
		return fetched;
	}

	public AbstractAction getAbstractAction(){
		return new AbstractAction(){
			public void actionPerformed(ActionEvent event){
				try {
					events.put(new EventOpInfo("keyPressed",event));
				} catch (Exception ex){}
			}
		};
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			events.put(new EventOpInfo("actionPerformed",e));
		} catch (Exception ex){}
	}

	public void itemStateChanged(ItemEvent e){
		try {
			events.put(new EventOpInfo("itemStateChanged",e));
		} catch (Exception ex){}
	}

	public void valueChanged(ListSelectionEvent event){
		try {
			events.put(new EventOpInfo("valueChanged",event));
		} catch (Exception ex){}
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowClosing(WindowEvent e) {
		try {
			events.put(new EventOpInfo("windowClosing",e));
		} catch (Exception ex){}
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			events.put(new EventOpInfo("mouseClicked",e));
		} catch (Exception ex){}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		try {
			events.put(new EventOpInfo("mouseEntered",e));
		} catch (Exception ex){}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		try {
			events.put(new EventOpInfo("mouseExited",e));
		} catch (Exception ex){}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			events.put(new EventOpInfo("mousePressed",e));
		} catch (Exception ex){}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		try {
			events.put(new EventOpInfo("mouseReleased",e));
		} catch (Exception ex){}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		try {
			events.put(new EventOpInfo("mouseDragged",e));
		} catch (Exception ex){}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		try {
			events.put(new EventOpInfo("mouseMoved",e));
		} catch (Exception ex){}
	}

	public void stateChanged(ChangeEvent e) {
		try {
			events.put(new EventOpInfo("stateChanged",e));
		} catch (Exception ex){}
	}	
}	
