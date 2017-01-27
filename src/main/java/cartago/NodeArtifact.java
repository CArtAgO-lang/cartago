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

import cartago.topology.TopologyException;
import cartago.topology.WorkspaceTree;
import cartago.topology.Utils;
import jason.asSyntax.Literal;
import jason.asSyntax.ASSyntax;
import cartago.ObsProperty;

/**
 * Artifact providing functionalities 
 * to manage/join workspaces and the node.
 *  
 * @author aricci
 *
 */
public class NodeArtifact extends Artifact {

	private WorkspaceKernel thisWsp;
	
	void init(WorkspaceKernel env){
		thisWsp = env;
		thisWsp.setArtifact(this);
		Literal list = ASSyntax.createLiteral("[]");
		defineObsProperty("topology_tree", list);
	}


	/**
	 * Join workspace local or remote
	 * 
	 * @param wspPath workspace path 
	 * @param res output parameter: workspace id
	 */
	@OPERATION void joinWorkspace(String wspPath, OpFeedbackParam<WorkspaceId> res) {
	    //determine if local or remote  
	    CartagoNode cnode = thisWsp.getNode();
	    WorkspaceTree tree = cnode.getTree();

	    WorkspaceId wId = thisWsp.getId();
	    try
		{
		    String artifactWorkspacePath = tree.getIdPath(wId);
		    WorkspaceId wId2 = tree.getPathId(wspPath);
		    
		    if(tree.inSameCartagoNode(wspPath, artifactWorkspacePath))
			joinLocalWorkspace(wId2, res);
		    else
			joinRemoteWorkspace(wId2, tree.getNodeAddressFromPath(wspPath), res);
		}
	    catch(TopologyException ex)
		{
		    ex.printStackTrace();
		}
	}

    
	/**
	 * Join a local workspace
	 * 
	 * @param wspName workspace name
	 * @param res output parameter: workspace id
	 */
	void joinLocalWorkspace(WorkspaceId wId, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
			CartagoWorkspace wsp = env.getNode().getWorkspace(wId);
			if (wsp!=null){
				WorkspaceKernel wspKernel = wsp.getKernel(); 
				ICartagoContext ctx = wspKernel.joinWorkspace(new cartago.AgentIdCredential(this.getCurrentOpAgentId().getGlobalId()), opFrame.getAgentListener());
				WorkspaceId wspId = ctx.getWorkspaceId();
				res.set(wspId);
				thisWsp.notifyJoinWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
				opFrame.setCompletionNotified();
			} else {
				failed("Workspace not available.");
			}
		} catch (Exception ex){
			//ex.printStackTrace();
			failed("Join Workspace error: "+ex.getMessage());
		}
	}
	
	/**
	 * Join a local workspace
	 * 
	 * @param wspName workspace name
	 * @param cred agent credentials
	 * @param res output parameter: workspace id
	 */
	void joinLocalWorkspace(WorkspaceId wId, AgentCredential cred, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
			CartagoWorkspace wsp = env.getNode().getWorkspace(wId);
			if (wsp!=null){
				WorkspaceKernel wspKernel = wsp.getKernel(); 
				ICartagoContext ctx = wspKernel.joinWorkspace(cred, opFrame.getAgentListener());
				WorkspaceId wspId = ctx.getWorkspaceId();
				res.set(wspId);
				thisWsp.notifyJoinWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
				opFrame.setCompletionNotified();
			} else {
				failed("Workspace not available.");
			}
		} catch (Exception ex){
			//ex.printStackTrace();
			failed("Join Workspace error");
		}
	}

	
	/**
	 * Join a remote workspace
	 * 
	 * @param wspName workspace name
	 * @param address address
	 * @param res output param: workspace id
	 */
	void joinRemoteWorkspace(WorkspaceId wId, String address, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
		    ICartagoContext ctx = CartagoService.joinRemoteWorkspace(wId, address, "default", new cartago.AgentIdCredential(this.getCurrentOpAgentId().getGlobalId()), opFrame.getAgentListener());
			WorkspaceId wspId = ctx.getWorkspaceId();
			res.set(wspId);
			thisWsp.notifyJoinWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
			opFrame.setCompletionNotified();
		} catch (Exception ex){
			//ex.printStackTrace();
			failed("Join Workspace error");
		}
	}
	
	/**
	 * Join a remote workspace
	 * 
	 * @param wspName workspace name
	 * @param address address
	 * @param infraServiceType infrastructure service type - "default" to use default one
	 * @param res output param: workspace id
	 */
	void joinRemoteWorkspace(WorkspaceId wId, String address, String infraServiceType, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
		    ICartagoContext ctx = CartagoService.joinRemoteWorkspace(wId, address, infraServiceType, new cartago.AgentIdCredential(this.getCurrentOpAgentId().getGlobalId()), opFrame.getAgentListener());
			WorkspaceId wspId = ctx.getWorkspaceId();
			res.set(wspId);
			thisWsp.notifyJoinWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
			opFrame.setCompletionNotified();
		} catch (Exception ex){
			//ex.printStackTrace();
			failed("Join Workspace error");
		}
	}

	/**
	 * 
	 * Join a remote workspace
     *
	 * @param wspName workspace name
	 * @param address address
	 * @param infraServiceType infrastructure service type - "default" to use default one
	 * @param roleName role to play
	 * @param cred agent credential
	 * @param res output param: workspace id 
	 */
	void joinRemoteWorkspace(WorkspaceId wId, String address, String infraServiceType, String roleName, AgentCredential cred, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
		    ICartagoContext ctx = CartagoService.joinRemoteWorkspace(wId, address, infraServiceType, cred, opFrame.getAgentListener());
			WorkspaceId wspId = ctx.getWorkspaceId();
			res.set(wspId);
			thisWsp.notifyJoinWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
			opFrame.setCompletionNotified();
		} catch (Exception ex){
			failed("Join Workspace error");
		}
	}

    public void specialSignal(String type, Object... objs)
    {
	ArtifactObsProperty[] changed = obsPropertyMap.getPropsChanged();
	ArtifactObsProperty[] added = obsPropertyMap.getPropsAdded();
	ArtifactObsProperty[] removed = obsPropertyMap.getPropsRemoved();	
	
	thisWsp.notifyObsEvent(getId(), new Tuple(type, objs), changed, added, removed);
	    //super.signal(type, objs);
    }
    

    @OPERATION void createWorkspace(String mountPoint)
    {
	try
	    {
		CartagoService.mount(mountPoint);
		//signal("created_workspace",mountPoint); now exeuted by infra
	    }
	catch(CartagoException ex)
	    {
		failed("Create workspace failed");
	    }
    }
    
	/**
	 * Create a workspace in the local node.
	 * 
	 * @param name name of the workspace
	 */
    //no longer supported
    /*void createWorkspace(String name){
		try {
			CartagoWorkspace wsp = env.getNode().createWorkspace(name);
			defineObsProperty("workspace",name,wsp.getId());
		} catch (Exception ex){
			failed("Workspace creation error");
		}
		}*/

	/**
	 * Experimental support for topology
	 * 
	 * Create a workspace in the local node.
	 * 
	 * @param name name of the workspace
	 */
	@OPERATION void createWorkspaceWithTopology(String name, String topologyClassName){
		try {
			CartagoWorkspace wsp = env.getNode().createWorkspace(name);
			AbstractWorkspaceTopology topology = (AbstractWorkspaceTopology) Class.forName(topologyClassName).newInstance();
			wsp.setTopology(topology);
			defineObsProperty("workspace",name,wsp.getId());
		} catch (Exception ex){
			failed("Workspace creation error");
		}
	}

	
	/**
	 * Get  node id
	 * @param param
	 */
	@OPERATION void getNodeId(OpFeedbackParam<NodeId> param){
		try {
			param.set(env.getNode().getId());
		} catch (Exception ex){
			failed("no_node_id");
		}
	}

	/**
	 * Enable linking with the specified node.
	 * 
	 * @param id
	 * @param support
	 * @param address
	 */
	@OPERATION void enableLinkingWithNode(NodeId id, String support, String address){
		CartagoService.enableLinkingWithNode(id, support, address);
	}
	
	/**
	 * Create a workspace in the local node.
	 * 
	 * @param name name of the workspace
	 */

        //no longer supported
	void createWorkspace(String name, ICartagoLogger logger){
		try {
			CartagoWorkspace wsp = env.getNode().createWorkspace(name,logger);
			defineObsProperty("workspace",name,wsp.getId());
		} catch (Exception ex){
			failed("Workspace creation error");
		}
	}

	/**
	 * Shutdown gracefully the node.
	 * 
	 * The dispose routine of all artifacts is called.
	 * 
	 */
	@OPERATION void shutdownNode(){
		try {
			CartagoService.shutdownNode();
		} catch (Exception ex){
			ex.printStackTrace();
			failed("Shutdown failed.");
		}
	}

	/**
	 * Shutdown the node and after one second shutdown the VM.
	 */
	@OPERATION void crash(){
		try {
			CartagoService.shutdownNode();
			Thread.sleep(1000);
			System.exit(0);
		} catch (Exception ex){
			failed("Shutdown failed.");
		}
	}
	
	/**
	 * Discover an workspace by name
	 * 
	 * @param workspaceName name of the workspace
	 * @param aid output param: workspace id 
	 */
	@LINK void lookupWorkspace(WorkspaceId wId, OpFeedbackParam<ArtifactId> aid){
		try {
			CartagoWorkspace wsp = env.getNode().getWorkspace(wId);
			if (wsp!=null){
				WorkspaceKernel wspKernel = wsp.getKernel();
				
				aid.set(wspKernel.getArtifact("workspace"));
			} else { //Maybe it is a remote workspace
				failed("Lookup workspace failed.");
			}
		} catch (Exception ex){
			failed(ex.toString());
		}
	}

    @OPERATION void quitWorkspace(String path)
    {
	try {
	    OpExecutionFrame opFrame = this.getOpFrame();
	    
	    //thisWsp.quitAgent(opFrame.getAgentId());
	    //WorkspaceId wspId = getId().getWorkspaceId();
	    CartagoNode cnode = thisWsp.getNode();
	    String address = cnode.getTree().getNodeAddressFromPath(path);
	    WorkspaceId wId = cnode.getTree().getPathId(path); 
	    CartagoService.quitWorkspace(address, wId, opFrame.getAgentId(), "default");
	    WorkspaceId wspId = cnode.getTree().getPathId(path);
	    
	    thisWsp.notifyQuitWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId);
	    opFrame.setCompletionNotified();
	} catch (Exception ex){
	    failed("Quit Workspace failed.");
	}
    }

    public void updateTreeProperty()
    {
	CartagoNode cnode = thisWsp.getNode();
	WorkspaceTree tree = cnode.getTree();

	ObsProperty prop = getObsProperty("topology_tree");
	String tt = tree.toList();
	Literal list = ASSyntax.createLiteral(tt);
	prop.updateValue(list);
    }
}
