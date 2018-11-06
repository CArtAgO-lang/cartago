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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
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
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
	 * Start observing an artifact of the workspace
	 * 
	 * @param aid the artifact id
	 */
	@Deprecated
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
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
	 * Start observing an artifact of the workspace
	 * 
	 * @param aid the artifact id
	 * @param filter filter to select which events to perceive
	 */
	@Deprecated
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
	 * @param artName artifact name
	 */
	@OPERATION void stopFocusing(String artName){
		AgentId userId = this.getCurrentOpAgentId();
		OpExecutionFrame opFrame = this.getOpFrame();
		try {
			ArtifactId aid = wspKernel.getArtifact(artName);
			List<ArtifactObsProperty> props = wspKernel.stopFocus(userId, opFrame.getAgentListener(), aid);
			wspKernel.notifyStopFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}
	
	/**
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
	 * Stop observing an artifact
	 * 
	 * @param aid
	 */
	@Deprecated
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

	/**
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
	 * Link two artifacts allowing to implement linked operations
	 * 
	 * @param artifactOutId id of the producer
	 * @param artifactOutPort port name used by the producer
	 * @param artifactInId id of the consumer
	 */
	@Deprecated
	@OPERATION void linkArtifacts(ArtifactId artifactOutId, String artifactOutPort, ArtifactId artifactInId){
		AgentId userId = this.getCurrentOpAgentId();
		try {
			wspKernel.linkArtifacts(userId, artifactOutId, artifactOutPort, artifactInId);
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

	/**
	 * Link two artifacts in both ways allowing to implement linked operations
	 * It uses
	 * 
	 * @param artifactA artifact name of 'A' producer/consumer
	 * @param artifactAPort port name used by 'A' producer/consumer
	 * @param artifactB artifact name of 'B' producer/consumer
	 * @param artifactBPort port name used by 'B' producer/consumer
	 */
	@OPERATION void linkArtifactsMutually(String artifactA, String artifactAPort, String artifactB, String artifactBPort){
		AgentId userId = this.getCurrentOpAgentId();
		try {
			ArtifactId aAid = wspKernel.getArtifact(artifactA);
			ArtifactId aBid = wspKernel.getArtifact(artifactB);
			wspKernel.linkArtifacts(userId, aAid, artifactAPort, aBid);
			wspKernel.linkArtifacts(userId, aBid, artifactBPort, aAid);
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}

	/**
	 * Link two artifacts in both ways allowing to implement linked operations
	 * It uses
	 * 
	 * @param artifactOutName artifact name of the producer
	 * @param artifactOutPort port name used by the producer
	 * @param artifactInName artifact name of the consumer
	 */
	@OPERATION void linkArtifactsOneWay(String artifactOutName, String artifactOutPort, String artifactInName){
		AgentId userId = this.getCurrentOpAgentId();
		try {
			ArtifactId aAid = wspKernel.getArtifact(artifactOutName);
			ArtifactId aBid = wspKernel.getArtifact(artifactInName);
			wspKernel.linkArtifacts(userId, aAid, artifactOutPort, aBid);
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}
	
	@OPERATION void quitWorkspace() {
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
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
	 * <p>Discover an artifact by name</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>artifactName (string) - name of the artifact.</li>
	 * </ul></p>
	 * 
	 * </ul></p>
	 */
	@Deprecated
	@OPERATION @LINK void lookupArtifact(String artifactName, OpFeedbackParam<ArtifactId> aid){
		try {
			ArtifactId id = wspKernel.lookupArtifact(this.getCurrentOpAgentId(),artifactName);
			aid.set(id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}

	/**
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
	 * <p>Discover an artifact by type</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>artifactName (string) - name of the artifact.</li>
	 * </ul></p>
	 * 
	 * </ul></p>
	 */
	@Deprecated
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
	 * Experimental operation to write in the file "graph.gv" the
	 * generate graph of environment components and relations
	 * 
	 * This method is temporary, this function sounds to be more useful
	 * if it is integrated to some interface instead of create a file
	 */
	@OPERATION void writeEnvironmentGraphInFile() {
		try (FileWriter fw = new FileWriter("graph.gv", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {

			String graph = wspKernel.generateGraph();
			out.print(graph);
			out.flush();
			out.close();
		} catch (Exception ex) {
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
	 * </ul></p>
	 */	
	@OPERATION @LINK void makeDynamicArtifact(String artifactName, String templateName, String sourceDir, Object[] param){
		try {
			ArtifactId id = wspKernel.makeDynamicArtifact(this.getCurrentOpAgentId(),artifactName,templateName,sourceDir,new ArtifactConfig(param));
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
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
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
	@Deprecated
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

	/**
	 * <p>Create or lookup and focus an artifact</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>name (String) -  name of the artifact</li>
	 * <li>template (String) -  artifact template</li>
	 * <li>param (ArtifactConfig) -  artifact configuration</li>
	 * </ul></p>
	 * 
	 * <p>Events generated:
	 * <ul>
	 * <li>new_artifact_created(name:String, template:String, id:ArtifactId) - if make succeeded</li>
	 * </ul></p>
	 */	
	@OPERATION @LINK void raiseArtifact(String artifactName, String templateName, Object[] params){
		try {
			// Create or lookup the artifact
			ArtifactId id = null;
			if (wspKernel.getArtifact(artifactName) == null) {
				id = wspKernel.makeDynamicArtifact(this.getCurrentOpAgentId(),artifactName,templateName,"",new ArtifactConfig(params));
			} else {
				id = wspKernel.getArtifact(artifactName);
			}
			
			// create property and perception
			if (this.getObsProperty("artifact") == null)
				this.defineObsProperty("artifact", artifactName, templateName, id);

			// focus artifact
			AgentId userId = this.getCurrentOpAgentId();
			OpExecutionFrame opFrame = this.getOpFrame();
			List<ArtifactObsProperty> props = wspKernel.focus(userId, null, opFrame.getAgentListener(), id);
			wspKernel.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), id, props);
			opFrame.setCompletionNotified();

		} catch (UnknownArtifactTemplateException ex){
			failed("artifact "+artifactName+" creation failed: unknown template "+templateName,"makeArtifactFailure","unknown_artifact_template",templateName);
		} catch (ArtifactAlreadyPresentException ex){
			failed("artifact "+artifactName+" creation failed: "+artifactName+"already present","makeArtifactFailure","artifact_already_present",artifactName);
		} catch (ArtifactConfigurationFailedException ex){
			failed("artifact "+artifactName+" creation failed: an error occurred in artifact initialisation","makeArtifactFailure","init_failed",artifactName);
		} catch (CartagoException e) {
			failed("Artifact Not Available.");
		}
	}

	/**
	 * <p>Destroy an artifact</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>name (String) -  name of the artifact</li>
	 * </ul></p>
	 */	
	@OPERATION @LINK void destroyArtifact(String artifactName){
		try {
			ArtifactId id = wspKernel.getArtifact(artifactName);
			wspKernel.disposeArtifact(this.getCurrentOpAgentId(),id);
			this.removeObsPropertyByTemplate("artifact", id.getName(), null, id);
		} catch (Exception ex){
			failed(ex.toString());
		}
	}

	/**
	 * @deprecated the methods that uses ArtifactId were replaced by methods using only artifact "name" 
	 * <p>Destroy an artifact</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>id (ArtifactId) -  id of the artifact</li>
	 * </ul></p>
	 */	
	@Deprecated
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
	 * @deprecated the methods that uses ArtifactId were replaced by methos using only artifact "name" 
	 * Get current artifact list.
	 * 
	 * 
	 */
	@Deprecated
	@LINK void getArtifactList(OpFeedbackParam<ArtifactId[]> artifacts) {
		artifacts.set(wspKernel.getArtifactIdList());
	}
	
	/**
	 * Set the default use policy
	 * 
	 */
	@OPERATION void getDefaultRolePolicy(String roleName, String artName, IArtifactUsePolicy policy) {
		try {
			wspKernel.getSecurityManager().setDefaultRolePolicy(roleName, policy);
		} catch(SecurityException ex){
			failed("security_exception");
		}
	}
	
}
