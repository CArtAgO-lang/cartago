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
package cartago;

import java.util.*;

import cartago.tools.inspector.Inspector;
import cartago.topology.WorkspaceTree;


/**
 * Class representing a CArtAgO node.
 * 
 * Not part of CArtAgO API.
 *  
 * @author aricci
 *
 */
public class CartagoNode {
	
	private HashMap<String,CartagoWorkspace> wsps;
	private NodeId nodeId;

        private WorkspaceTree tree = null;
	
	CartagoNode() throws CartagoException {
		wsps = new HashMap<String,CartagoWorkspace>();
		nodeId = new NodeId();
		// create main workspace		
		createWorkspace(CartagoService.MAIN_WSP_NAME);		
	}



    
    //version with new topology support
    CartagoNode(String wspName, WorkspaceTree tree) throws CartagoException {
		wsps = new HashMap<String,CartagoWorkspace>();
		nodeId = new NodeId();
		// creates a main workspace with a specified name		               
		createWorkspace(wspName);
		this.tree = tree;
	}	

	CartagoNode(ICartagoLogger logger) throws CartagoException {
		wsps = new HashMap<String,CartagoWorkspace>();
		nodeId = new NodeId();
		// create default workspace		
		createWorkspace(CartagoService.MAIN_WSP_NAME ,logger);		
	}	

	/* experimental support to topology */
	
	CartagoNode(AbstractWorkspaceTopology topology) throws CartagoException {
		wsps = new HashMap<String,CartagoWorkspace>();
		nodeId = new NodeId();
		// create default workspace		
		createWorkspace(CartagoService.MAIN_WSP_NAME,topology);
		
	}	
	
	/**
	 * Create a workspace inside the node.
	 * 
	 * @param name workspace name
	 * @return
	 */
	public synchronized CartagoWorkspace createWorkspace(String name) throws CartagoException {
		CartagoWorkspace wsp = wsps.get(name);		
		if (wsp==null){
			WorkspaceId wid = new WorkspaceId(name,nodeId); 
			wsp = new CartagoWorkspace(wid,this);
			wsps.put(name, wsp);
			return wsp;
		} else {
			throw new CartagoException("workspace already created");
		}
	}

	/**
	 * Create a workspace inside the node.
	 * 
	 * @param name workspace name
	 * @param log logger
	 * @return
	 */
	public synchronized CartagoWorkspace createWorkspace(String name, ICartagoLogger log) throws CartagoException {
		CartagoWorkspace wsp = wsps.get(name);		
		if (wsp==null){
			WorkspaceId wid = new WorkspaceId(name,nodeId); 
			wsp = new CartagoWorkspace(wid,this,log);
			wsps.put(name, wsp);
			return wsp;
		} else {
			throw new CartagoException("workspace already created");
		}
	}
	
	/* experimental */
	/**
	 * Create a workspace inside the node.
	 * 
	 * @param name workspace name
	 * @return
	 */
	public synchronized CartagoWorkspace createWorkspace(String name, AbstractWorkspaceTopology topology) throws CartagoException {
		CartagoWorkspace wsp = wsps.get(name);		
		if (wsp==null){
			WorkspaceId wid = new WorkspaceId(name,nodeId); 
			wsp = new CartagoWorkspace(wid,this);
			wsp.setTopology(topology);
			wsps.put(name, wsp);
			return wsp;
		} else {
			throw new CartagoException("workspace already created");
		}
	}
	
	
	/**
	 * Get the reference to a workspace of the node.
	 * 
	 * @param wspName workspace name
	 * @return
	 * @throws CartagoException
	 */
	public synchronized CartagoWorkspace getWorkspace(String wspName) throws CartagoException {
		CartagoWorkspace env = wsps.get(wspName);		
		if (env==null){
			throw new CartagoException("Workspace not found.");
		}
		return env;
	}
	
	
	
	/**
	 * Manage the execution of an inter-artifact op, possibly between artifacts 
	 * belonging to different workspaces of this node.
	 * 
	 */
	OpId execInterArtifactOp(ICartagoCallback ctx, long actionId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws InterruptedException, OpRequestTimeoutException, OperationUnavailableException, ArtifactNotAvailableException, CartagoException  {
		WorkspaceId targetWsp = targetId.getWorkspaceId();
		if (targetWsp.getNodeId().equals(nodeId)){
			CartagoWorkspace wsp = wsps.get(targetWsp.getName());
			if (wsp==null){
				throw new ArtifactNotAvailableException();
			} else {
				return wsp.execInterArtifactOp(ctx, actionId, userId, srcId, targetId, op, timeout, test);
			}
		} else {
			return CartagoService.execRemoteInterArtifactOp(ctx, actionId, userId, srcId, targetId, op, timeout, test);
		}
	}
		
	
	/**
	 * Shutdown the node.
	 */
	synchronized void shutdown(){
		for (CartagoWorkspace wsp:wsps.values()){
			wsp.getKernel().shutdown();
		}
	}
		
	/**
	 * Get the node id
	 * 
	 * @return
	 */
	public NodeId getId(){
		return nodeId;
	}

    public WorkspaceTree getTree()
    {
	return this.tree;
    }
}
