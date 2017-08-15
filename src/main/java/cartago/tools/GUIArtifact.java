/**
 * CArtAgO - DEIS, University of Bologna
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cartago.tools;

import cartago.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * Base class for defining GUI artifacts
 * 
 * @author aricci
 *
 */
public abstract class GUIArtifact extends Artifact {

	private HashMap<Object,HashMap<String,String>> evToOpLinks;
	private boolean stopped;
	private CommonEventListener proc;
	private JFrame mainFrame;
	
    protected GUIArtifact() {
    	stopped = false;
		evToOpLinks = new HashMap<Object,HashMap<String,String>>();
    	proc = new CommonEventListener();
    }

    protected void init(){
    	setup();
    	execInternalOp("fetchGUIEvents");
    }
    
    /**
     * Set the GUI Artifact main frame
     * 
     * @param frame
     */
    protected void setMainFrame(JFrame frame){
    	mainFrame = frame;
    }
    
    /**
     * This method can be override to initialize the GUI
     */
    public void setup(){}
    

    @OPERATION void selectFileToOpen(String currentDir, String fileDesc, String[] extensions, OpFeedbackParam<String> selectedFile){
    	File file = new File(currentDir);
    	JFileChooser chooser = new JFileChooser(file);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(fileDesc, extensions);
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          // System.out.println("You chose to open this file: " +
        	selectedFile.set(chooser.getSelectedFile().getAbsolutePath());
        } else {
        	failed("no_file_selected");
        }
    }
    
    @INTERNAL_OPERATION void fetchGUIEvents(){
    	while (!stopped){
    		await(proc);
    		EventOpInfo eventOp = proc.getCurrentEventFetched();
    		HashMap<String,String> map = evToOpLinks.get(eventOp.getEvent().getSource());
    		if (map!=null){
    			String opName = map.get(eventOp.getListenerName());
    			if (opName!=null){
    				execInternalOp(opName,eventOp.getEvent());
    			}
    		}
    	}
    }
    
    /**
     * Link a action event to a specific operation of the artifact
     * 
     * @param source event source
     * @param opName name of the operation to trigger
     */
	protected void linkActionEventToOp(AbstractButton source, String opName){
		insertEventToOp(source,"actionPerformed",opName);
		source.addActionListener(getEventListenerInstance());
	}

	   /**
     * Link a action event to a specific operation of the artifact
     * 
     * @param source event source
     * @param opName name of the operation to trigger
     */
	protected void linkActionEventToOp(JComboBox source, String opName){
		insertEventToOp(source,"actionPerformed",opName);
		source.addActionListener(getEventListenerInstance());
	}
	
	/**
     * Link a mouse event to a specific operation of the artifact
     * 
     * @param source event source
     * @param mouseEventType specific mouse event (mouseClicked, mouseEntered, .., mouseDragged, mouseMoved)
     * @param opName name of the operation to trigger
     */
	protected void linkMouseEventToOp(JComponent source, String mouseEventType, String opName){
		insertEventToOp(source,mouseEventType,opName);
		source.addMouseListener(getEventListenerInstance());
		source.addMouseMotionListener(getEventListenerInstance());
	}

	/**
     * Link a mouse event to a specific operation of the artifact
     * 
     * @param source event source
     * @param mouseEventType specific mouse event (mouseClicked, mouseEntered, .., mouseDragged, mouseMoved)
     * @param opName name of the operation to trigger
     */
	protected void linkMouseEventToOp(JFrame source, String mouseEventType, String opName){
		insertEventToOp(source,mouseEventType,opName);
		source.addMouseListener(getEventListenerInstance());
		source.addMouseMotionListener(getEventListenerInstance());
	}

	/**
     * Link a item event to a specific operation of the artifact
     * 
     * @param source event source
     * @param opName name of the operation to trigger
     */
	protected void linkItemEventToOp(ItemSelectable source, String opName){
		insertEventToOp(source,"itemStateChanged",opName);
		source.addItemListener(getEventListenerInstance());
	}

    /**
     * Link a list selection event to a specific operation of the artifact
     * 
     * @param source event source
     * @param opName name of the operation to trigger
     */
	protected void linkListSelectionEventToOp(JList source, String opName){
		insertEventToOp(source,"valueChanged",opName);
		source.addListSelectionListener(getEventListenerInstance());
	}

	
	/**
     * Link a window closing event to a specific operation of the artifact
     * 
     * @param source event source
     * @param opName name of the operation to trigger
     */
	protected void linkWindowClosingEventToOp(JFrame source, String opName){
		insertEventToOp(source,"windowClosing", opName);
		source.addWindowListener(getEventListenerInstance());
	}
	
	/**
     * Link a key stroke event to a specific operation of the artifact
     *
	 * @param source event source
	 * @param key key
	 * @param opName operation name
	 */
	protected void linkKeyStrokeToOp(JComponent source, String key, String opName){		
		insertEventToOp(source,"keyPressed", opName);
		source.getInputMap().put(KeyStroke.getKeyStroke(key), "myAction");
		source.getActionMap().put("myAction",getEventListenerInstance().getAbstractAction());
	}

	
	/**
     * Link a state change event for JSliders to a specific operation of the artifact
     * 
     * @param source event source
     * @param opName name of the operation to trigger
     */
	protected void linkChangeEventToOp(JSlider source, String opName){
		insertEventToOp(source,"stateChanged",opName);
		source.addChangeListener(getEventListenerInstance());
	}

	
	/**
	 * Register a new handle for a specific type pf event
	 * 
	 * @param obj source
	 * @param type event type
	 * @param opName internal op to exec
	 */
	protected void insertEventToOp(Object obj, String type, String opName){
		HashMap<String,String> map = evToOpLinks.get(obj);
		if (map==null){
			map = new HashMap<String,String>();
			evToOpLinks.put(obj, map);
		}
		map.put(type, opName);
	}

	/**
	 * Get the common event listener used by the artifact internally to process Swing event
	 * 
	 * @return
	 */
	protected CommonEventListener getEventListenerInstance(){
		return proc;
	}

}
