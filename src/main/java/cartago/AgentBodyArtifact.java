package cartago;

import java.util.List;

public class AgentBodyArtifact extends Artifact {
	
	/* defines the radius within which the agent can perceive artifacts*/
	protected double observingRadius;

	private AgentBody body;
	
	void init(AgentBody body){
		this.body = body;
		/* legacy CArtAgO < 3.0 */
		// defineObsProperty("joined",body.getWorkspaceId().getName(),body.getWorkspaceId()); 
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
			boolean wasAlreadyObserving = body.isObserving(aid);
			List<ArtifactObsProperty> props = wsp.focus(userId, null, opFrame.getAgentListener(), aid);
			wsp.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			// defineObsProperty("focused", aid.getWorkspaceId().getName(), aid.getName(), aid);
			opFrame.setCompletionNotified();
			if (!wasAlreadyObserving) {
				defineObsProperty("focusing", aid, aid.getName(), aid.getArtifactType(), aid.getWorkspaceId(), aid.getWorkspaceId().getName(), aid.getWorkspaceId().getFullName());
			}
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
			boolean wasAlreadyObserving = body.isObserving(aid);
			List<ArtifactObsProperty> props = wsp.focus(userId, filter, opFrame.getAgentListener(), aid);
			wsp.notifyFocusCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), aid, props);
			// defineObsProperty("focused", aid.getWorkspaceId().getName(), aid.getName(), aid);
			opFrame.setCompletionNotified();
			if (!wasAlreadyObserving) {
				defineObsProperty("focusing", aid, aid.getName(), aid.getArtifactType(), aid.getWorkspaceId(), aid.getWorkspaceId().getName(), aid.getWorkspaceId().getFullName());
			}
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
				defineObsProperty("focusing", aid, aid.getName(), aid.getArtifactType(), aid.getWorkspaceId(), aid.getWorkspaceId().getName(), aid.getWorkspaceId().getFullName());
				// defineObsProperty("focused", aid.getWorkspaceId().getName(), aid.getName(), aid);
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
			defineObsProperty("focusing", aid, aid.getName(), aid.getArtifactType(), aid.getWorkspaceId(), aid.getWorkspaceId().getName(), aid.getWorkspaceId().getFullName());
			// defineObsProperty("focused", aid.getWorkspaceId().getName(), aid.getName(), aid);
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
			// this.removeObsPropertyByTemplate("focused", null, null, aid);
			removeObsPropertyByTemplate("focusing", null, null, aid, null, null, null);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact Not Available.");
		}
	}
	
	
	protected void setupPosition(AbstractWorkspacePoint pos, double observabilityRadius, double observingRadius){
		position = pos;
		this.observabilityRadius = observabilityRadius;
		this.observingRadius = observingRadius;
		wsp.bindAgentBodyArtifact(this.getCreatorId(),this);
		try {
			wsp.notifyArtifactPositionOrRadiusChange(this.getId());	
			wsp.notifyAgentPositionOrRadiusChange(getCreatorId());	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	protected void updatePosition(AbstractWorkspacePoint pos) {
		position = pos;
		try {
			wsp.notifyArtifactPositionOrRadiusChange(getId());	
			wsp.notifyAgentPositionOrRadiusChange(getCreatorId());	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	protected double getObservingRadius(){
		return observingRadius;
	}
	
	protected void setObservingRadius(double radius){
		observingRadius = radius;
		try {
			wsp.notifyAgentPositionOrRadiusChange(this.getCreatorId());	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	/* called directly by the kernel */
	
	/*
	public  void addFocusedArtifact(ArtifactId id, String artName, String artType, WorkspaceId wspId){
		defineObsProperty("focusing", id, artName, artType, wspId);
	}
	
	void removeFocusedArtifact(ArtifactId id){
		this.removeObsPropertyByTemplate("focusing", id, null, null, null);
	}
	*/
	
	
}
