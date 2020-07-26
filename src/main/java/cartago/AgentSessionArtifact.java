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

import cartago.security.SecurityException;
import cartago.tools.inspector.Inspector;
import cartago.security.*;
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
	private CartagoSession session;
	
	@OPERATION void init(AgentCredential cred, ICartagoCallback eventListener, CartagoSession session, Workspace homeWsp){
		this.cred = cred;
		this.eventListener = eventListener;
		this.homeWsp = homeWsp;
		this.session = session;
	}
	
	/**
	 * Join a workspace
	 * 
	 * @param wspName workspace name
	 * @param res output parameter: workspace id
	 */
	@OPERATION void joinWorkspace(String wspRef, OpFeedbackParam<WorkspaceId> res) {
		try {			
			WorkspaceId implicitWspId = session.getCurrentWorkspace();
			if (!wspRef.startsWith("/")) {
				wspRef = implicitWspId.getFullName() + "/" + wspRef;
				/* action on the workspace artifact of the implicit workspace */
			} 			
			wspRef = removeRelativePath(wspRef);
			WorkspaceDescriptor des = CartagoEnvironment.getInstance().resolveWSP(wspRef);
		    OpExecutionFrame opFrame = this.getOpFrame();
			if (des.isLocal()) {
				/* use only the name */
				try {
					ICartagoContext ctx = des.getWorkspace().joinWorkspace(cred, null, eventListener);
					WorkspaceId wspId = ctx.getWorkspaceId();
					res.set(wspId);
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
					WorkspaceId wspId = ctx.getWorkspaceId();
					// this.implicitWspId = wspId;
					res.set(wspId);
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

	@OPERATION void mountWorkspace(String remoteWspPath, String localFullName,  String protocol)  {
		try {
			WorkspaceId implicitWspId = session.getCurrentWorkspace();
			if (!localFullName.startsWith("/")) {
				localFullName = implicitWspId.getFullName() + "/" + localFullName;
				/* action on the workspace artifact of the implicit workspace */
			} 			
			localFullName = removeRelativePath(localFullName);
			int index = localFullName.lastIndexOf('/');
			
			String parentPath = localFullName.substring(0, index);
			String wspName = localFullName.substring(index + 1);
			
			WorkspaceDescriptor des = CartagoEnvironment.getInstance().resolveWSP(parentPath);
			if (des.isLocal()) {
				des.getWorkspace().mountWorkspace(remoteWspPath, wspName, protocol);
			} else {
				failed("not implemented");
				/*
				try {
					ICartagoContext ctx = CartagoEnvironment.getInstance().joinRemoteWorkspace(des.getRemotePath(), des.getAddress(), des.getProtocol(), cred, eventListener);
					WorkspaceId wspId = ctx.getWorkspaceId();
					this.implicitWspId = wspId;
					res.set(wspId);
				} catch (Exception ex) {
					// ex.printStackTrace();
					failed("Join Workspace error: "+ex.getMessage());
					
				}*/
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			failed("Mount Workspace error: "+ex.getMessage());
		}
	}

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
	
	@OPERATION void quitWorkspace(WorkspaceId wspId) {
		try {
			ICartagoContext ctx = session.getJoinedWsp(wspId);
			ctx.quit();
		} catch (Exception ex){
			failed("Quit Workspace failed.");
		}
	}
	
	
}
