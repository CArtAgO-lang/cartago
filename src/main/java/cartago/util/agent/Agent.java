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
package cartago.util.agent;

import cartago.*;
import cartago.security.*;
import cartago.events.*;


/**
 * Base class for defining simple Java agents on top of CArtAgO
 * 
 * @author aricci
 *
 */
public class Agent extends Thread {

	private CartagoBasicContext ctx;
	protected static Object[] NO_PARAMS = new Object[0];
	
	public Agent(String agentName) {
		super(agentName);
		ctx = new CartagoBasicContext(agentName,CartagoEnvironment.MAIN_WSP_NAME);
	}

	public Agent(String agentName, String workspaceName, String workspaceHost) {
		super(agentName);
		ctx = new CartagoBasicContext(agentName, workspaceName, workspaceHost);
	}
	
	protected String getAgentName(){
		return ctx.getName();
	}

	/**
	 * Gets the id of a joined workspace
	 * 
	 * @param name
	 * @return
	 * @throws CartagoException
	 */
	protected WorkspaceId getJoinedWorkspaceId(String name) throws CartagoException {
		return ctx.getJoinedWspId(name);
	}

	protected ActionFeedback doActionAsync(Op op) throws CartagoException {
		return ctx.doActionAsync(op);
	}


	protected ActionFeedback doActionAsync(ArtifactId aid, Op op, long timeout) throws CartagoException {
		return ctx.doActionAsync(aid, op, timeout);
	}

	protected ActionFeedback doActionAsync(Op op, long timeout) throws CartagoException {
		return ctx.doActionAsync(op, timeout);
	}

	protected void doAction(Op op, long timeout) throws ActionFailedException, CartagoException {
		ctx.doAction(op, timeout);
	}

	protected void doAction(Op op) throws ActionFailedException, CartagoException {
		this.doAction(op, -1);
	}

	protected void doAction(ArtifactId aid, Op op, long timeout) throws ActionFailedException, CartagoException {
		ctx.doAction(aid, op, timeout);
	}

	protected void doAction(ArtifactId aid, Op op) throws ActionFailedException, CartagoException {
		this.doAction(aid,op,-1);
	}

	protected Percept fetchPercept() throws InterruptedException {
		return ctx.fetchPercept();
	}

	protected Percept waitForPercept() throws InterruptedException {
		return ctx.waitForPercept();
	}

	protected Percept waitForPercept(IEventFilter filter) throws InterruptedException {
		return ctx.waitForPercept(filter);
	}
	
	protected void waitFor(long ms){
		try {
			Thread.sleep(ms);
		} catch(Exception ex){}
	}

	protected void log(String msg){
		System.out.println("["+ctx.getName()+"] "+msg);
	}


	//Utility methods

	protected WorkspaceId joinWorkspace(String wspName, AgentCredential cred) throws CartagoException {
		return ctx.joinWorkspace(wspName, cred);
	}

	protected WorkspaceId joinRemoteWorkspace(String wspName, String address, String roleName, AgentCredential cred)  throws CartagoException {
		return ctx.joinRemoteWorkspace(wspName, address, roleName, cred);
	}

	protected ArtifactId lookupArtifact(WorkspaceId id, String artifactName) throws CartagoException {
		return ctx.lookupArtifact(id, artifactName);
	}

	protected ArtifactId lookupArtifact(String artifactName) throws CartagoException {
		return ctx.lookupArtifact(artifactName);
	}

	protected ArtifactId discoverArtifact(String artifactName) throws CartagoException {
		ArtifactId aid = null;
		while (aid == null) {
			try {
				aid = lookupArtifact(artifactName);
			} catch (Exception ex){
				waitFor(100);
			}
		}
		return aid;
	}
	
	protected ArtifactId discoverArtifact(WorkspaceId id, String artifactName) throws CartagoException {
		ArtifactId aid = null;
		while (aid == null) {
			try {
				aid = lookupArtifact(id,artifactName);
			} catch (Exception ex){
				waitFor(100);
			}
		}
		return aid;
	}
	
	
	protected ArtifactId makeArtifact(WorkspaceId id, String artifactName, String templateName) throws CartagoException {
		return ctx.makeArtifact(id,artifactName, templateName);
	}

	protected ArtifactId makeArtifact(WorkspaceId id, String artifactName, String templateName, Object[] params) throws CartagoException {
		return ctx.makeArtifact(id,artifactName, templateName, params);
	}
	
	protected ArtifactId makeArtifact(String artifactName, String templateName) throws CartagoException {
		return ctx.makeArtifact(artifactName, templateName);
	}

	protected ArtifactId makeArtifact(String artifactName, String templateName, Object[] params) throws CartagoException {
		return ctx.makeArtifact(artifactName, templateName, params);
	}

	protected void disposeArtifact(ArtifactId artifactId) throws CartagoException {
		ctx.disposeArtifact(artifactId);
	}

	protected void focus(ArtifactId artifactId) throws CartagoException {
		ctx.focus(artifactId);
	}

	protected void focus(ArtifactId artifactId, IEventFilter filter) throws CartagoException {
		ctx.focus(artifactId, filter);
	}

	protected void stopFocus(ArtifactId aid) throws CartagoException {
		ctx.stopFocus(aid);
	}
	
	protected ArtifactObsProperty getObsProperty(String name){
		return ctx.getObsProperty(name);
	}
}