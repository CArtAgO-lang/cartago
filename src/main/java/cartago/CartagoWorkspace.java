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

import cartago.security.IWorkspaceSecurityManager;
import cartago.security.SecurityException;


/**
 * Class representing a Cartago workspace.
 * 
 * @author aricci
 * 
 */
public class CartagoWorkspace {
	
	private WorkspaceKernel wspKernel;
	private boolean securityManagerEnabled;
	
	/**
	 * Create an  workspace
	 * 
	 * @param name logic name of the environment
	 */
	public CartagoWorkspace(WorkspaceId id, CartagoNode node){
		securityManagerEnabled = false;
		wspKernel = new WorkspaceKernel(id,node,null);
	}

	
	/**
	 * Create an  workspace
	 * 
	 * @param name logic name of the environment
	 */
	public CartagoWorkspace(WorkspaceId id, CartagoNode node, ICartagoLogger logger){
		securityManagerEnabled = false;
		wspKernel = new WorkspaceKernel(id,node,logger);
	}
	
	public WorkspaceId getId(){
		return wspKernel.getId();
	}
	
	public CartagoNode getNode(){
		return wspKernel.getNode();
	}
			
	/**
	 * Get a context to work inside the workspace
	 * 
	 * @param aid Agent identifier
	 * @return
	 */
	public ICartagoContext join(AgentCredential cred, ICartagoCallback agentCallback) throws SecurityException, CartagoException {		
		return wspKernel.joinWorkspace(cred, agentCallback);
	}

	public void quitAgent(AgentId agentId) throws CartagoException {
		wspKernel.quitAgent(agentId);
	}
	
	/**
	 * Exec an inter-artifact op
	 * 
	 */
	public OpId execInterArtifactOp(ICartagoCallback callback, long agentCallbackId, AgentId uid, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws CartagoException  {
		return wspKernel.execInterArtifactOp(callback, agentCallbackId, uid, srcId, targetId, op, timeout, test);
	}
	
	
	// 
	
	public void enableSecurityManager(){
		wspKernel.setSecurityManager(new cartago.security.DefaultSecurityManager());
		securityManagerEnabled = true;
	}
		
	public void setSecurityManager(IWorkspaceSecurityManager man){
		wspKernel.setSecurityManager(man);
		securityManagerEnabled = true;
	}

	public IWorkspaceSecurityManager getSecurityManager() throws SecurityException {
		if (securityManagerEnabled){
			return wspKernel.getSecurityManager();
		} else {
			throw new SecurityException("No security manager enabled.");
		}
	}
	
	//
	
	public   void registerLogger(ICartagoLogger logger){
		wspKernel.getLoggerManager().registerLogger(logger);
	}

	public   void unregisterLogger(ICartagoLogger logger){
		wspKernel.getLoggerManager().unregisterLogger(logger);
	}		
	
	//
	
	public ICartagoController getController(){
		return wspKernel.getController();
	}
	
	
	/* experimental */
	public void setTopology(AbstractWorkspaceTopology topology){
		wspKernel.setWSPTopology(topology);
		
	}
	//
	
	public void setLoggerManager(ICartagoLoggerManager man){
		wspKernel.setLoggerManager(man);
	}
	
	public ICartagoLoggerManager getLoggerManager(){
		return wspKernel.getLoggerManager();
	}
	
	public WorkspaceKernel getKernel(){
		return wspKernel;
	}
}
