package cartago.tools.inspector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import javax.swing.*;
import cartago.*;
import java.awt.*;
import java.awt.event.*;

public class InspectorView extends JFrame {		
	
	InspectorPanel inspPanel;
	
	public InspectorView(WorkspaceScene scene){
		setTitle("Workspace Inspector");
		setSize(800,600);

		inspPanel = new InspectorPanel(scene,10,10,410,410);
		setContentPane(inspPanel);
	}
	
	public void repaint(){
		super.repaint();
		inspPanel.repaint();
	}
}

class InspectorPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
	
	WorkspaceScene scene;
	int cx,cy;
	int x0,y0,x1,y1;
	
	double posX0 = 0;
	double posY0 = 0;
    double factor = 1;
    
	boolean pressedLeft, pressedRight;
	int xpos, xposPrev, ypos, yposPrev;
	JButton zoomIn, zoomOut;
	
	AgentMarker draggedAgent;
	ArtifactMarker draggedArtifact;
	
	public InspectorPanel(WorkspaceScene scene, int x0, int y0, int x1, int y1){
		this.scene = scene;
		cx = (x1-x0)/2 + x0;
		cy = (y1-y0)/2 + y0;
		pressedLeft = false;
		pressedRight = false;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		zoomIn = new JButton("zoom +");
		zoomOut = new JButton("zoom -");
        this.add(zoomIn);		
        this.add(zoomOut);	
        zoomIn.addActionListener(this);
        zoomOut.addActionListener(this);
	}
	
	private void log(String msg){
		System.out.println("[VIEW] "+msg);
	}
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);    

	    Graphics2D g2 = (Graphics2D)g;
        
	    g2.setColor(Color.WHITE);
	    g2.fillRect(0, 0, 800, 600);
	    
	    //log("painting.");
	    scene.lock();

	    ArrayList<AgentMarker> agList= scene.getAgents();
	    ArrayList<ArtifactMarker> arList= scene.getArtifacts();
	    Collection<OpInfo> ops = scene.getOngoingOps();
	    Collection<FocussedArtifactInfo> obs = scene.getFocussedArtifacts();
	    
	    g2.setColor(Color.BLUE);
	    for (AgentMarker m: agList){
	    	P2d pos = m.getPos();
	    	int x = (int)(cx + (pos.x-posX0)*(x1 - cx)*factor);
	    	int y = (int)(cy - (pos.y-posY0)*(cy - y0)*factor);
	    	m.cacheViewPos(x, y);
	        g2.fillOval(x-4, y-4, 8, 8);
	        //log("draw agent "+m.getAgentId()+" pos "+pos+" in "+x+" "+y);
	        g2.drawString(m.getAgentId().getAgentName(), x-32, y+20);
	    }
	    
	    g2.setColor(Color.BLACK);
	    for (ArtifactMarker m: arList){
	    	P2d pos = m.getPos();
	    	int x = (int)(cx + (pos.x-posX0)*(x1 - cx)*factor);
	    	int y = (int)(cy - (pos.y-posY0)*(cy - y0)*factor);
	    	m.cacheViewPos(x, y);
	        g2.fillRect(x-4, y-4, 8, 8);
	        //log("draw artifact "+m.getArtifactId()+" pos "+pos+" in "+x+" "+y);
	        g2.drawString(m.getArtifactId().getName(), x-32, y+20);
	    }
	    
	    g2.setColor(Color.BLUE);
	    for (OpInfo op: ops){
	    	// log("Op info: "+op.getAgentId()+" "+op.getTargetId()+" id "+op.getOpId());
	    	P2d pos = getAgentPos(agList,op.getAgentId());
	    	if (pos!=null){
		    	int ax = (int)(cx + (pos.x-posX0)*(x1 - cx)*factor);
		    	int ay = (int)(cy - (pos.y-posY0)*(cy - y0)*factor);
		    	P2d pos1 = getArtifactPos(arList,op.getTargetId());
		    	if (pos1!=null){
		    	    int bx = (int)(cx + (pos1.x-posX0)*(x1 - cx)*factor);
			    	int by = (int)(cy - (pos1.y-posY0)*(cy - y0)*factor);
			    	g2.drawLine(ax, ay, bx, by);
			    	g2.drawString(op.getOp().getName(), (ax + bx)/2, (ay + by)/2);
		    	} else {
		    		// log("!ERROR! artifact not avail: "+op.getTargetId()+" for op "+op.getOp());
		    	}
	    	} else {
	    		// log("!ERROR! agent not avail: "+op.getAgentId()+" for op "+op.getOp());
	    	}
	    }
	  
	    g2.setColor(Color.YELLOW);
	    for (FocussedArtifactInfo info: obs){
	    	// log("Op info: "+op.getAgentId()+" "+op.getTargetId()+" id "+op.getOpId());
	    	P2d pos = getAgentPos(agList,info.getAgentId());
	    	if (pos!=null){
		    	int ax = (int)(cx + (pos.x-posX0)*(x1 - cx)*factor);
		    	int ay = (int)(cy - (pos.y-posY0)*(cy - y0)*factor);
		    	P2d pos1 = getArtifactPos(arList,info.getTargetId());
		    	if (pos1!=null){
		    	    int bx = (int)(cx + (pos1.x-posX0)*(x1 - cx)*factor);
			    	int by = (int)(cy - (pos1.y-posY0)*(cy - y0)*factor);
			    	g2.drawLine(ax, ay, bx, by);
		    	} else {
		    		log("!ERROR! artifact not avail: "+info.getTargetId());
		    	}
	    	} else {
	    		log("!ERROR! agent not avail: "+info.getAgentId());
	    	}
	    }

	    scene.unlock();
	    
	}	


	private P2d getAgentPos(ArrayList<AgentMarker>  list, AgentId id){
		for (AgentMarker marker: list){
			if (marker.getAgentId().equals(id)){
				return marker.getPos();
			}
		}
		// log(">>>> ERROR Looking for "+id+" - "+list.size());
		return null;
	}

	private P2d getArtifactPos(ArrayList<ArtifactMarker>  list, ArtifactId id){
		for (ArtifactMarker marker: list){
			if (marker.getArtifactId().equals(id)){
				return marker.getPos();
			}
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getButton() == MouseEvent.BUTTON1){
			pressedLeft = true;
		} else if (e.getButton() == MouseEvent.BUTTON2){
			pressedRight = true;
		}
		scene.lock();
		//System.out.println("check for hiting..."+e.getX()+" "+e.getY());
		draggedArtifact = null;
		for (ArtifactMarker marker: scene.getArtifacts()){
			if (marker.hit(e.getX(), e.getY(), 5)){
				draggedArtifact = marker;
			    break;
			}
		}
		draggedAgent = null;
		if (draggedArtifact == null){
			for (AgentMarker marker: scene.getAgents()){
				if (marker.hit(e.getX(), e.getY(), 5)){
					draggedAgent = marker;
				    break;
				}
			}
		}
		scene.unlock();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getButton() == MouseEvent.BUTTON1){
			pressedLeft = false;
		} else if (e.getButton() == MouseEvent.BUTTON2){
			pressedRight = false;
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		xposPrev = xpos;
		yposPrev = ypos;
		xpos = e.getX();
		ypos = e.getY();
		
		int dx = xpos - xposPrev;
		int dy = ypos - yposPrev;
		
		if (pressedLeft){
			if (draggedArtifact != null){
				double newx = (((double)draggedArtifact.getViewPosX()+dx) - cx)/(factor*(x1-cx))+draggedArtifact.getPos().x;
				double newy = ((double)(cy - (draggedArtifact.getViewPosY()+dy)))/(factor*(cy-y0))+draggedArtifact.getPos().y;
				draggedArtifact.getPos().x = newx;
				draggedArtifact.getPos().y = newy;
                
			} else if (draggedAgent != null) {
				
			} else {
				if (dx > 2){
					posX0 += 0.01;
				} else if (dx < -2) {
					posX0 -= 0.01;
				}
				if (dy > 2){
					posY0 += 0.01;
				} else if (dy < -2){
					posY0 -= 0.01;
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==zoomIn){
			factor *= 1.05;
		} else if (e.getSource()==zoomOut){
			factor /= 1.05;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
