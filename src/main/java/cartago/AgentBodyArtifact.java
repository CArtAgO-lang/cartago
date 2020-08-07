package cartago;

public class AgentBodyArtifact extends Artifact {
	
	/* defines the radius within which the agent can perceive artifacts*/
	protected double observingRadius;

	private AgentBody body;
	
	@OPERATION void init(AgentBody body){
		this.body = body;
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
	
	public  void addFocusedArtifact(ArtifactId id, String artName, String artType, WorkspaceId wspId){
		defineObsProperty("focusing", id, artName, artType, wspId);
	}
	
	void removeFocusedArtifact(ArtifactId id){
		this.removeObsPropertyByTemplate("focusing", id, null, null, null);
	}
	
}
