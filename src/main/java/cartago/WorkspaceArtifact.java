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
 * Artifact providing basic functionalities to manage the workspace,
 * including creating new artifacts, lookup artifacts, setting RBAC
 * security policies, and so on.
 * 
 * @author aricci
 *
 */
public class WorkspaceArtifact extends Artifact {

	private Workspace wsp;
	private Inspector debug;
	
	@OPERATION void init(Workspace env){
		this.wsp = env;
	}

	/* Wsp management */

	/**
	 * Create a child workspace of this workspace.
	 * 
	 * @param name name of the workspace
	 */
	@OPERATION void createWorkspace(String name){
		try {
			wsp.createWorkspace(name);
			defineObsProperty("childWsp",name);
		} catch (Exception ex){
			failed("Workspace creation error");
		}
	}

	/**
	 * Create a child workspace running on a different node
	 * 
	 * @param name name of the workspace
	 */
	@OPERATION void createWorkspace(String name, String address){
		try {
			wsp.createWorkspaceOnRemoteNode(name, address, CartagoEnvironment.getInstance().getDefaultInfrastructureLayer(), null);
			defineObsProperty("childWsp",name);
		} catch (Exception ex){
			failed("Workspace creation error");
		}
	}

	/**
	 * 
	 * Create a child workspace specifying a topology
	 * 
	 * @param name name of the workspace
	 */
	@OPERATION void createWorkspaceWithTopology(String name, String topologyClassName){
		try {
			WorkspaceDescriptor des = wsp.createWorkspace(name);
			AbstractWorkspaceTopology topology = (AbstractWorkspaceTopology) Class.forName(topologyClassName).newInstance();
			des.getWorkspace().setWSPTopology(topology);
			defineObsProperty("childWsp",name);
		} catch (Exception ex){
			failed("Workspace creation error");
		}
	}
	

	/**
	 * Link an existing workspace (either locally or in another MAS) to be reachable from this workspace
	 * 
	 * @param wspToBeLinkedPath
	 * @param localName
	 */
	@OPERATION void linkWorkspace(String wspToBeLinkedPath, String localName)  {
		try {
			wsp.linkWorkspace(wspToBeLinkedPath, localName);	
			defineObsProperty("linkedWsp",localName,wspToBeLinkedPath);

		} catch (Exception ex) {
			// ex.printStackTrace();
			failed("link workspace error: "+ex.getMessage());
		}
	}
	
	/**
	 * Link a workspace to another workspace
	 * 
	 * @param fromWspFullPath full path of the workspace where to create the link
	 * @param toWspFullPath full path of the workspace to be linked
	 * @param localName link name
	 */
	@OPERATION void linkWorkspaces(String fromWspFullPath, String toWspFullPath, String localName)  {
		try {
			WorkspaceDescriptor fromWspDescriptor = CartagoEnvironment.getInstance().resolveWSP(fromWspFullPath);
			if (fromWspDescriptor.isLocal()) {
				fromWspDescriptor.getWorkspace().linkWorkspace(toWspFullPath, localName);	
			} else {
				failed("not implemented");
			}
		} catch (Exception ex) {
			failed("Link Workspace error: "+ex.getMessage());
		}
	}

	
	/**
	 * Link an existing remote workspace to be reachable from this workspace
	 * 
	 * @param remoteWspPath
	 * @param wspName
	 * @param protocol
	 */
	@OPERATION void linkRemoteWorkspace(String remoteWspPath, String wspName,  String protocol)  {
		try {
			wsp.linkRemoteWorkspace(remoteWspPath, wspName, protocol);	
			defineObsProperty("linkedWsp",wspName,remoteWspPath);
		} catch (Exception ex) {
			// ex.printStackTrace();
			failed("link workspace error: "+ex.getMessage());
		}
	}

	/**
	 * Link an existing remote workspace to be reachable from this workspace
	 * 
	 * @param remoteWspPath
	 * @param wspName
	 * @param protocol
	 */
	@OPERATION void linkRemoteWorkspace(String remoteWspPath, String wspName)  {
		try {
			wsp.linkRemoteWorkspace(remoteWspPath, wspName, "web");	
			defineObsProperty("linkedWsp",wspName,remoteWspPath);
		} catch (Exception ex) {
			// ex.printStackTrace();
			failed("link workspace error: "+ex.getMessage());
		}
	}

	
	/**
	 * Mounting is linking a remote wsp specifying the full local path 
	 * 
	 * @param remoteWspPath
	 * @param localFullName
	 * @param protocol
	 */
	@OPERATION void mountWorkspace(String targetMASURL, String remoteWspPath, String localWspPath)  {
		try {
			localWspPath = removeRelativePath(localWspPath);
			int index = localWspPath.lastIndexOf('/');
			
			String parentPath = localWspPath.substring(0, index);
			String wspName = localWspPath.substring(index + 1);
			
			WorkspaceDescriptor des = CartagoEnvironment.getInstance().resolveWSP(parentPath);
			if (des.isLocal()) {
				des.getWorkspace().linkRemoteWorkspace(targetMASURL + remoteWspPath, wspName, "web");
				ArtifactId wid = des.getWorkspace().getWspArtifactId();
				ArtifactDescriptor wspArtifactDes = des.getWorkspace().getArtifactDescriptor(wid.getName());
				((WorkspaceArtifact)wspArtifactDes.getArtifact()).registerNewLinkedWsp(wspName, remoteWspPath);
			} else {
				failed("not implemented");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			failed("Mount Workspace error: "+ex.getMessage());
		}
	}

	private void registerNewLinkedWsp(String wspName, String remoteWspPath) {
		try {
			this.beginExtSession();
			defineObsProperty("linkedWsp",wspName,remoteWspPath);
			this.endExtSession();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.endExtSessionWithFailure();
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
	
	
	/* Artifacts management */


	/**
	 * <p>Discover an artifact by name</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>artifactName (string) - name of the artifact.</li>
	 * </ul></p>
	 * 
	 * </ul></p>
	 */
	@OPERATION @LINK void lookupArtifact(String artifactName, OpFeedbackParam<ArtifactId> aid){
		try {
			ArtifactId id = wsp.lookupArtifact(this.getCurrentOpAgentId(),artifactName);
			aid.set(id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}

	/**
	 * <p>Discover an artifact by type</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>artifactName (string) - name of the artifact.</li>
	 * </ul></p>
	 * 
	 * </ul></p>
	 */
	@OPERATION @LINK void lookupArtifactByType(String artifactType, OpFeedbackParam<ArtifactId> aid){
		try {
			ArtifactId id = wsp.lookupArtifactByType(this.getCurrentOpAgentId(),artifactType);
			aid.set(id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}

	/**
	 * <p>Get the name list of available artifacts </p>
	 * 
	 * </ul></p>
	 */
	@OPERATION @LINK void getCurrentArtifacts(OpFeedbackParam<String[]> list){
		try {
			String[] names = wsp.getArtifactList();
			list.set(names);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}
	
	/**
	 * <p>Create a new artifact</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>name (String) -  name of the artifact</li>
	 * <li>template (String) -  artifact template</li>
	 * <li>param (ArtifactConfig) -  artifact configuration</li>
	 * <li>aid [feedback] - artifact id </li>
	 * </ul></p>
	 */	
	@OPERATION @LINK void makeArtifact(String artifactName, String templateName){
		try {
			ArtifactId id = wsp.makeArtifact(this.getCurrentOpAgentId(),artifactName,templateName,ArtifactConfig.DEFAULT_CONFIG);
			// signal("new_artifact_created",artifactName,templateName,id);
			this.defineObsProperty("artifact", id, artifactName, templateName);
		} catch (UnknownArtifactTemplateException ex){
			failed("artifact "+artifactName+" creation failed: unknown template "+templateName,"makeArtifactFailure","unknown_artifact_template",templateName);
		} catch (ArtifactAlreadyPresentException ex){
			failed("artifact "+artifactName+" creation failed: "+artifactName+"already present","makeArtifactFailure","artifact_already_present",artifactName);
		} catch (ArtifactConfigurationFailedException ex){
			failed("artifact "+artifactName+" creation failed: an error occurred in artifact initialisation","makeArtifactFailure","init_failed",artifactName);
		}
	}	

	/**
	 * <p>Create a new artifact</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>name (String) -  name of the artifact</li>
	 * <li>template (String) -  artifact template</li>
	 * <li>param (ArtifactConfig) -  artifact configuration</li>
	 * <li>aid [feedback] - artifact id </li>
	 * </ul></p>
	 * 
	 * </ul></p>
	 */	
	@OPERATION @LINK void makeArtifact(String artifactName, String templateName, Object[] param){
		try {
			ArtifactId id = wsp.makeArtifact(this.getCurrentOpAgentId(),artifactName,templateName,new ArtifactConfig(param));
			this.defineObsProperty("artifact", id, artifactName, templateName);
		} catch (UnknownArtifactTemplateException ex){
			failed("artifact "+artifactName+" creation failed: unknown template "+templateName,"makeArtifactFailure","unknown_artifact_template",templateName);
		} catch (ArtifactAlreadyPresentException ex){
			failed("artifact "+artifactName+" creation failed: "+artifactName+"already present","makeArtifactFailure","artifact_already_present",artifactName);
		} catch (ArtifactConfigurationFailedException ex){
			failed("artifact "+artifactName+" creation failed: an error occurred in artifact initialisation","makeArtifactFailure","init_failed",artifactName);
		}
	}

	
	/**
	 * <p>Create a new artifact</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>name (String) -  name of the artifact</li>
	 * <li>template (String) -  artifact template</li>
	 * <li>param (ArtifactConfig) -  artifact configuration</li>
	 * <li>aid [feedback] - artifact id </li>
	 * </ul></p>
	 * 
	 * <p>Events generated:
	 * <ul>
	 * <li>new_artifact_created(name:String, template:String, id:ArtifactId) - if make succeeded</li>
	 * </ul></p>
	 */	
	@OPERATION @LINK void makeArtifact(String artifactName, String templateName, Object[] params, OpFeedbackParam<ArtifactId> aid){
		try {
			ArtifactId id = wsp.makeArtifact(this.getCurrentOpAgentId(),artifactName,templateName,new ArtifactConfig(params));
			aid.set(id);
			this.defineObsProperty("artifact", id, artifactName, templateName);
		} catch (UnknownArtifactTemplateException ex){
			failed("artifact "+artifactName+" creation failed: unknown template "+templateName,"makeArtifactFailure","unknown_artifact_template",templateName);
		} catch (ArtifactAlreadyPresentException ex){
			failed("artifact "+artifactName+" creation failed: "+artifactName+"already present","makeArtifactFailure","artifact_already_present",artifactName);
		} catch (ArtifactConfigurationFailedException ex){
			failed("artifact "+artifactName+" creation failed: an error occurred in artifact initialisation","makeArtifactFailure","init_failed",artifactName);
		}
	}

	

	@OPERATION @LINK void disposeArtifact(ArtifactId id){
		try {
			wsp.disposeArtifact(this.getCurrentOpAgentId(),id);
		} catch (Exception ex){
			failed(ex.toString());
			return;
		}
		try {
			// some artifact (session_XXX for instance) are not registered 
			// here and the removeObsProo could fail
			this.removeObsPropertyByTemplate("artifact", id, null, null);
		} catch (Exception ex) {
		}
	}
	
	@GUARD boolean artifactAvailable(String artName){
		return wsp.getArtifact(artName) != null;
	}

		
	@OPERATION void linkArtifacts(ArtifactId artifactOutId, String artifactOutPort, ArtifactId artifactInId){
		AgentId userId = this.getCurrentOpAgentId();
		try {
			wsp.linkArtifacts(userId, artifactOutId, artifactOutPort, artifactInId);
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}
	
	/* Artifact factories management */
	
	/**
	 * Add an artifact factory
	 * 
	 * @param factory artifact factory
	 */
	@OPERATION @LINK void addArtifactFactory(ArtifactFactory factory){
		wsp.addArtifactFactory(factory);
	}


	/**
	 * Remove an existing artifact factory
	 * 
	 * @param name
	 */
	@OPERATION @LINK void removeArtifactFactory(String name){
		wsp.removeArtifactFactory(name);
	}
	
	
	/* WSP Rule management */
	
	@OPERATION void setWSPRuleEngine(AbstractWSPRuleEngine man){
		try {
			man.setKernel(wsp);
			wsp.setWSPRuleEngine(man);
		} catch(Exception ex){
			failed(ex.getMessage());
		}
	}
	
	@OPERATION void addWSPRuleEngine(AbstractWSPRuleEngine man){
		try {
			man.setKernel(wsp);
			wsp.addWSPRuleEngine(man);
		} catch(Exception ex){
			failed(ex.getMessage());
		}
	}

	/* Topology management */
	
	/*
	 @OPERATION void setWorkspaceTopology(AbstractWorkspaceTopology topology){
		try {
			wspKernel.setWSPTopology(topology);
		} catch(Exception ex){
			failed(ex.getMessage());
		}
	}
	*/
	
	// manuals management

/*	
	
	@LINK @OPERATION void createManual(String src, OpFeedbackParam<ArtifactId> aid) throws CartagoException {
		try {
			Manual man = env.execCreateManual(src);
			ArtifactId id = env.(getOpUserId(),man.getName(),"alice.cartago.ManualArtifact",new ArtifactConfig(man),null);
			aid.set(id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}

	@LINK @OPERATION void createManualFromFile(String fname, OpFeedbackParam<ArtifactId> aid) throws CartagoException {
		try {
			String src = env.loadManualSrc(fname);
			Manual man = env.createManual(src, getOpUserId());
			ArtifactId id = env.execMakeArtifact(getOpUserId(),man.getName(),"alice.cartago.ManualArtifact",new ArtifactConfig(man),null);
			aid.set(id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}
*/
	
	/* RBAC */
	
	@OPERATION void setSecurityManager(IWorkspaceSecurityManager man){
		wsp.setSecurityManager(man);
	}
	
	/**
	 * Add a role.
	 */
	@OPERATION void addRole(String roleName){
		try {
			wsp.getSecurityManager().addRole(roleName);
		} catch(SecurityException ex){
			failed("security_exception");
		}
	}

	/**
	 * Remove a role, if it exists
	 * 
	 * @param roleName
	 */
	@OPERATION void removeRole(String roleName) {
		try {
			wsp.getSecurityManager().removeRole(roleName);
		} catch(SecurityException ex){
			failed("security_exception");
		}
	}

	/**
	 * Get current roles list.
	 * 
	 * @return
	 */
	@OPERATION void getRoleList(OpFeedbackParam<String[]> list) {
		try {
			list.set(wsp.getSecurityManager().getRoleList());
		} catch(SecurityException ex){
			failed("security_exception");
		}
	}
	

	/**
	 * Add a policy to a role 
	 * 
	 */
	@OPERATION void addRolePolicy(String roleName, String  artifactName, IArtifactUsePolicy policy){
		try {
			wsp.getSecurityManager().addRolePolicy(roleName, artifactName, policy);
		} catch(SecurityException ex){
			failed("security_exception");
		}
	}

	/**
	 * Remove a policy
	 * 
	 */
	@OPERATION void removeRolePolicy(String roleName, String  artifactName){
		try {
			wsp.getSecurityManager().removeRolePolicy(roleName, artifactName);
		} catch(SecurityException ex){
			failed("security_exception");
		}
	}

	/**
	 * Set the default use policy
	 * 
	 */
	@OPERATION void setDefaultRolePolicy(String roleName, String artName, IArtifactUsePolicy policy) {
		try {
			wsp.getSecurityManager().setDefaultRolePolicy(roleName, policy);
		} catch(SecurityException ex){
			failed("security_exception");
		}
	}

	/**
	 * Get current artifact list.
	 * 
	 * 
	 */
	@LINK void getArtifactList(OpFeedbackParam<ArtifactId[]> artifacts) {
		artifacts.set(wsp.getArtifactIdList());
	}

	/**
	 * Enable debugging
	 * 
	 */
	@OPERATION void enableDebug(){
		 if (debug == null){
			 Inspector insp = new Inspector();
			 insp.start();
			 wsp.getLoggerManager().registerLogger(insp.getLogger());
		 }
	}
	
	/**
	 * Disable debugging
	 * 
	 */
	@OPERATION void disableDebug(){
		 if (debug != null){
			 wsp.getLoggerManager().unregisterLogger(debug.getLogger());
		 }
	}

}
