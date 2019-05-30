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

	/* wsp management */

	/**
	 * Create a workspace in the local node.
	 * 
	 * @param name name of the workspace
	 */
	@OPERATION void createWorkspace(String name){
		try {
			WorkspaceDescriptor des = wsp.createWorkspace(name);
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
			WorkspaceDescriptor des = wsp.createWorkspace(name);
			AbstractWorkspaceTopology topology = (AbstractWorkspaceTopology) Class.forName(topologyClassName).newInstance();
			des.getWorkspace().setWSPTopology(topology);
			defineObsProperty("workspace",name,wsp.getId());
		} catch (Exception ex){
			failed("Workspace creation error");
		}
	}
	

	@GUARD boolean artifactAvailable(String artName){
		return wsp.getArtifact(artName) != null;
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
		
	@OPERATION void linkArtifacts(ArtifactId artifactOutId, String artifactOutPort, ArtifactId artifactInId){
		AgentId userId = this.getCurrentOpAgentId();
		try {
			wsp.linkArtifacts(userId, artifactOutId, artifactOutPort, artifactInId);
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

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
			this.defineObsProperty("artifact", artifactName, templateName, id);
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
			this.defineObsProperty("artifact", artifactName, templateName, id);
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
			this.defineObsProperty("artifact", artifactName, templateName, id);
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
			this.removeObsPropertyByTemplate("artifact", id.getName(), null, id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}
	
	
	/**
	 * Start observing an artifact of the workspace
	 * 
	 * @param aid the artifact id
	 */
	@OPERATION void focus(ArtifactId aid){
		AgentId userId = this.getCurrentOpAgentId();
		OpExecutionFrame opFrame = this.getOpFrame();
		try {
			List<ArtifactObsProperty> props = wsp.focus(userId, null, opFrame.getAgentListener(), aid);
			wsp.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

	/**
	 * Start observing an artifact of the workspace
	 * 
	 * @param aid the artifact id
	 * @param filter filter to select which events to perceive
	 */
	@OPERATION void focus(ArtifactId aid, IEventFilter filter){
		AgentId userId = this.getCurrentOpAgentId();
		OpExecutionFrame opFrame = this.getOpFrame();
		try {
			List<ArtifactObsProperty> props = wsp.focus(userId, filter, opFrame.getAgentListener(), aid);
			wsp.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}
	
	/**
	 * Start observing an artifact as soon as it is available
	 * 
	 * @param artName artifact name
	 */
	@OPERATION void focusWhenAvailable(String artName){
		AgentId userId = this.getCurrentOpAgentId();
		OpExecutionFrame opFrame = this.getOpFrame();
		try {
			ArtifactId aid = null;
			while (aid == null){
				await("artifactAvailable", artName);		
				aid = wsp.getArtifact(artName);
			}
			List<ArtifactObsProperty> props = wsp.focus(userId, null, opFrame.getAgentListener(), aid);
			wsp.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

	
	
	/**
	 * Start observing an artifact as soon as it is available
	 * 
	 * @param artName artifact name
	 * @param filter a filter to select the events to perceive
	 */
	@OPERATION void focusWhenAvailable(String artName, IEventFilter filter){
		AgentId userId = this.getCurrentOpAgentId();
		OpExecutionFrame opFrame = this.getOpFrame();
		try {
			ArtifactId aid = null;
			while (aid == null){
				await("artifactAvailable", artName);		
				aid = wsp.getArtifact(artName);
			}
			List<ArtifactObsProperty> props = wsp.focus(userId, filter, opFrame.getAgentListener(), aid);
			wsp.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

	/**
	 * Stop observing an artifact
	 * 
	 * @param aid
	 */
	@OPERATION void stopFocus(ArtifactId aid){
		AgentId userId = this.getCurrentOpAgentId();
		OpExecutionFrame opFrame = this.getOpFrame();
		try {
			List<ArtifactObsProperty> props = wsp.stopFocus(userId, opFrame.getAgentListener(), aid);
			wsp.notifyStopFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
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
}
