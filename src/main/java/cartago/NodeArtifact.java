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

import cartago.security.AgentCredential;
import cartago.security.AgentIdCredential;

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
	}

	/**
	 * Join a local workspace
	 * 
	 * @param wspName workspace name
	 * @param res output parameter: workspace id
	 */
	@OPERATION void joinWorkspace(String wspName, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
			CartagoWorkspace wsp = CartagoNode.getInstance().getWorkspace(wspName);
			if (wsp!=null){
				WorkspaceKernel wspKernel = wsp.getKernel(); 
				ICartagoContext ctx = wspKernel.joinWorkspace(new cartago.security.AgentIdCredential(this.getCurrentOpAgentId().getGlobalId()), opFrame.getAgentListener());
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
	@OPERATION void joinWorkspace(String wspName, AgentCredential cred, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
			CartagoWorkspace wsp = CartagoNode.getInstance().getWorkspace(wspName);
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
	@OPERATION void joinRemoteWorkspace(String wspName, String address, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
		    ICartagoContext ctx = CartagoService.joinRemoteWorkspace(wspName, address, "default", new cartago.security.AgentIdCredential(this.getCurrentOpAgentId().getGlobalId()), opFrame.getAgentListener());
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
	@OPERATION void joinRemoteWorkspace(String wspName, String address, String infraServiceType, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
		    ICartagoContext ctx = CartagoService.joinRemoteWorkspace(wspName, address, infraServiceType, new cartago.security.AgentIdCredential(this.getCurrentOpAgentId().getGlobalId()), opFrame.getAgentListener());
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
	@OPERATION void joinRemoteWorkspace(String wspName, String address, String infraServiceType, String roleName, AgentCredential cred, OpFeedbackParam<WorkspaceId> res) {
		try {
		    OpExecutionFrame opFrame = this.getOpFrame();
		    ICartagoContext ctx = CartagoService.joinRemoteWorkspace(wspName, address, infraServiceType, cred, opFrame.getAgentListener());
			WorkspaceId wspId = ctx.getWorkspaceId();
			res.set(wspId);
			thisWsp.notifyJoinWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
			opFrame.setCompletionNotified();
		} catch (Exception ex){
			failed("Join Workspace error");
		}
	}
	
	/**
	 * Create a workspace in the local node.
	 * 
	 * @param name name of the workspace
	 */
	@OPERATION void createWorkspace(String name){
		try {
			CartagoWorkspace wsp = CartagoNode.getInstance().createWorkspace(name);
			defineObsProperty("workspace",name,wsp.getId());
		} catch (Exception ex){
			failed("Workspace creation error");
		}
	}

	/**
	 * Experimental support for topology
	 * 
	 * Create a workspace in the local node.
	 * 
	 * @param name name of the workspace
	 */
	@OPERATION void createWorkspaceWithTopology(String name, String topologyClassName){
		try {
			CartagoWorkspace wsp = CartagoNode.getInstance().createWorkspace(name);
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
			param.set(CartagoNode.getInstance().getId());
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
	@OPERATION void createWorkspace(String name, ICartagoLogger logger){
		try {
			CartagoWorkspace wsp = CartagoNode.getInstance().createWorkspace(name,logger);
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
	@LINK void lookupWorkspace(String wspName, OpFeedbackParam<ArtifactId> aid){
		try {
			CartagoWorkspace wsp = CartagoNode.getInstance().getWorkspace(wspName);
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
}
