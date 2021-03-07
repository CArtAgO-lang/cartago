/**
 * CArtAgO - DISI, University of Bologna
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

/**
 * Artifact providing basic functionalities to manage the working session
 * of an agent inside the MAS environment.
 * 
 * This is a personal artifact, one for each agent.
 * 
 * This artifact is stored in the home workspace of the agent.
 * 
 * @author aricci
 *
 */
public class AgentSessionArtifact extends Artifact {

	private AgentCredential cred;
	private ICartagoCallback eventListener;
	private Workspace homeWsp;
	private AgentSession session;
	
	@OPERATION void init(AgentCredential cred, ICartagoCallback eventListener, AgentSession session, Workspace homeWsp){
		this.cred = cred;
		this.eventListener = eventListener;
		this.homeWsp = homeWsp;
		this.session = session;
		WorkspaceId wspId = homeWsp.getId();
		this.defineObsProperty("joinedWsp",wspId, wspId.getName(), wspId.getFullName());

	}
	
	/**
	 * Join a workspace
	 * 
	 * @param wspRef workspace absolute name
	 * @param res output parameter: workspace id
	 */
	@OPERATION void joinWorkspace(String wspRef, OpFeedbackParam<WorkspaceId> res) {
		try {			
			// wspRef must be absolute: /...
			
			wspRef = removeRelativePath(wspRef);
			WorkspaceDescriptor des = CartagoEnvironment.getInstance().resolveWSP(wspRef);
		    OpExecutionFrame opFrame = this.getOpFrame();

		    /* check if wsp already joined */
		    WorkspaceId wspId = des.getId();
		    ICartagoContext cachedCtx = session.getJoinedWsp(wspId);
		    if (cachedCtx != null) {
				if (res != null) {
					res.set(wspId);
				}
				des.getWorkspace().notifyJoinWSPCompleted(eventListener, opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, cachedCtx);
				return;
		    }
		    
			if (des.isLocal()) {
				/* use only the name */
				try {
					ICartagoContext ctx = des.getWorkspace().joinWorkspace(cred, null, eventListener);
					wspId = ctx.getWorkspaceId();
					if (res != null) {
						res.set(wspId);
					}
					this.defineObsProperty("joinedWsp", wspId, wspId.getName(), wspId.getFullName());
					
					// this.implicitWspId = wspId;
					des.getWorkspace().notifyJoinWSPCompleted(eventListener, opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
				} catch (Exception ex){
					//ex.printStackTrace();
					failed("Join Workspace error: "+ex.getMessage());
				}
			} else {
				try {
					ICartagoContext ctx = CartagoEnvironment.getInstance()
							.joinRemoteWorkspace(CartagoEnvironment.getInstance().getName(), des.getAddress(), des.getRemotePath(), des.getProtocol(), cred, eventListener, wspRef);
					wspId = ctx.getWorkspaceId();
					// this.implicitWspId = wspId;
					if (res != null) {
						res.set(wspId);
					}
					this.defineObsProperty("joinedWsp", wspId, wspId.getName(), wspId.getFullName());					
					homeWsp.notifyJoinWSPCompleted(eventListener, opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId, ctx);
				} catch (Exception ex) {
					// ex.printStackTrace();
					failed("Join Workspace error: "+ex.getMessage());
					
				}
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			failed("Join Workspace error: "+ex.getMessage());
		}
		
	}	

	/**
	 * Join a workspace
	 * 
	 * @param wspRef workspace absolute name
	 * @param res output parameter: workspace id
	 */
	@OPERATION void joinWorkspace(String wspRef) {
		joinWorkspace(wspRef, null);
	}	
	
	/**
	 * Quit from the environment.
	 * 
	 * @param wspId
	 */
	@OPERATION void quitFromEnvironment() {
		try {
			for (WorkspaceId wspId: session.getJoinedWorkspaces()) {
				try {
					ICartagoContext ctx = session.getJoinedWsp(wspId);
					ctx.quit();
					this.removeObsPropertyByTemplate("joinedWsp",  wspId, null, null);
				} catch (Exception ex){
					failed("Quit from Workspace " + wspId +" failed.");
				}
			}
		} catch (Exception ex){
			failed("quit from env failed: " + ex.getLocalizedMessage());
		}
	}

	/**
	 * Quit from a joined workspace
	 * 
	 * @param wspId
	 */
	@OPERATION void quitWorkspace(WorkspaceId wspId) {
		try {
			ICartagoContext ctx = session.getJoinedWsp(wspId);
			ctx.quit();
			this.removeObsPropertyByTemplate("joinedWsp",  wspId, null, null);
		} catch (Exception ex){
			failed("Quit Workspace failed.");
		}
	}
	
	
	
	// aux
	
	private String removeRelativePath(String path) {
		String[] parts = path.split("/");
		List<String> list = new ArrayList<String>();
		for (String p: parts) {
			if (!p.equals("")) {
				list.add(p);
			}
		}
		int index = 0;
		while (index < list.size()) {
			String elem = list.get(index);
			if (elem.equals(".")) {
				list.remove(index);
			} else if (elem.equals("..")) {
				list.remove(index);
				if (list.size() > 0) {
					list.remove(index - 1);
				} else {
					return null;
				}
			} else {
				index++;
			}
		}
		StringBuffer sb = new StringBuffer();
		for (String s: list) {
			sb.append("/"+s);
		}
		return sb.toString();
	}
	
	
	
}
