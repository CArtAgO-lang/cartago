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

	private WorkspaceKernel wspKernel;
	private Inspector debug;
	
	@OPERATION void init(WorkspaceKernel env){
		this.wspKernel = env;
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
			List<ArtifactObsProperty> props = wspKernel.focus(userId, null, opFrame.getAgentListener(), aid);
			wspKernel.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
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
			List<ArtifactObsProperty> props = wspKernel.focus(userId, filter, opFrame.getAgentListener(), aid);
			wspKernel.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
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
				aid = wspKernel.getArtifact(artName);
			}
			List<ArtifactObsProperty> props = wspKernel.focus(userId, null, opFrame.getAgentListener(), aid);
			wspKernel.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
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
				aid = wspKernel.getArtifact(artName);
			}
			List<ArtifactObsProperty> props = wspKernel.focus(userId, filter, opFrame.getAgentListener(), aid);
			wspKernel.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

	@GUARD boolean artifactAvailable(String artName){
		return wspKernel.getArtifact(artName) != null;
	}

	/**
	 * Enable debugging
	 * 
	 */
	@OPERATION void enableDebug(){
		 if (debug == null){
			 Inspector insp = new Inspector();
			 insp.start();
			 wspKernel.getLoggerManager().registerLogger(insp.getLogger());
		 }
	}
	
	/**
	 * Disable debugging
	 * 
	 */
	@OPERATION void disableDebug(){
		 if (debug != null){
			 wspKernel.getLoggerManager().unregisterLogger(debug.getLogger());
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
			List<ArtifactObsProperty> props = wspKernel.stopFocus(userId, opFrame.getAgentListener(), aid);
			wspKernel.notifyStopFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}
	
	@OPERATION void linkArtifacts(ArtifactId artifactOutId, String artifactOutPort, ArtifactId artifactInId){
		AgentId userId = this.getCurrentOpAgentId();
		try {
			wspKernel.linkArtifacts(userId, artifactOutId, artifactOutPort, artifactInId);
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

    //@OPERATION moved to NodeArtifact
	void quitWorkspace() {
		try {
			OpExecutionFrame opFrame = this.getOpFrame();
			wspKernel.quitAgent(opFrame.getAgentId());
			WorkspaceId wspId = getId().getWorkspaceId();
			wspKernel.notifyQuitWSPCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), wspId);
			opFrame.setCompletionNotified();
		} catch (Exception ex){
			failed("Quit Workspace failed.");
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
			ArtifactId id = wspKernel.lookupArtifact(this.getCurrentOpAgentId(),artifactName);
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
			ArtifactId id = wspKernel.lookupArtifactByType(this.getCurrentOpAgentId(),artifactType);
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
			String[] names = wspKernel.getArtifactList();
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
		wspKernel.addArtifactFactory(factory);
	}


	/**
	 * Remove an existing artifact factory
	 * 
	 * @param name
	 */
	@OPERATION @LINK void removeArtifactFactory(String name){
		wspKernel.removeArtifactFactory(name);
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
			ArtifactId id = wspKernel.makeArtifact(this.getCurrentOpAgentId(),artifactName,templateName,ArtifactConfig.DEFAULT_CONFIG);
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
			ArtifactId id = wspKernel.makeArtifact(this.getCurrentOpAgentId(),artifactName,templateName,new ArtifactConfig(param));
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
			ArtifactId id = wspKernel.makeArtifact(this.getCurrentOpAgentId(),artifactName,templateName,new ArtifactConfig(params));
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
			wspKernel.disposeArtifact(this.getCurrentOpAgentId(),id);
			this.removeObsPropertyByTemplate("artifact", id.getName(), null, id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}
	
	/* WSP Rule management */
	
	@OPERATION void setWSPRuleEngine(AbstractWSPRuleEngine man){
		try {
			man.setKernel(wspKernel);
			wspKernel.setWSPRuleEngine(man);
		} catch(Exception ex){
			failed(ex.getMessage());
		}
	}

	/* Topology management */
	
	@OPERATION void setWorkspaceTopology(AbstractWorkspaceTopology topology){
		try {
			wspKernel.setWSPTopology(topology);
		} catch(Exception ex){
			failed(ex.getMessage());
		}
	}
	
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
		wspKernel.setSecurityManager(man);
	}
	
	/**
	 * Add a role.
	 */
	@OPERATION void addRole(String roleName){
		try {
			wspKernel.getSecurityManager().addRole(roleName);
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
		wspKernel.getSecurityManager().removeRole(roleName);
	}

	/**
	 * Get current roles list.
	 * 
	 * @return
	 */
	@OPERATION void getRoleList(OpFeedbackParam<String[]> list) {
		list.set(wspKernel.getSecurityManager().getRoleList());
	}
	

	/**
	 * Add a policy to a role 
	 * 
	 */
	@OPERATION void addRolePolicy(String roleName, String  artifactName, IArtifactUsePolicy policy){
		try {
			wspKernel.getSecurityManager().addRolePolicy(roleName, artifactName, policy);
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
			wspKernel.getSecurityManager().removeRolePolicy(roleName, artifactName);
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
			wspKernel.getSecurityManager().setDefaultRolePolicy(roleName, policy);
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
		artifacts.set(wspKernel.getArtifactIdList());
	}
}
