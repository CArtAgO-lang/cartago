package cartago;

public class AgentBodyArtifact extends Artifact {
	
	/* defines the radius within which the agent can perceive artifacts*/
	protected double observingRadius;

	private AgentBody body;
	
	@OPERATION void init(AgentBody body){
		this.body = body;
		defineObsProperty("joined_wsp",body.getWorkspaceId().getName(),body.getWorkspaceId()); 
	}
	
	
	protected void setupPosition(AbstractWorkspacePoint pos, double observabilityRadius, double observingRadius){
		position = pos;
		this.observabilityRadius = observabilityRadius;
		this.observingRadius = observingRadius;
		env.bindAgentBodyArtifact(this.getCreatorId(),this);
		try {
			env.notifyArtifactPositionOrRadiusChange(this.getId());	
			env.notifyAgentPositionOrRadiusChange(getCreatorId());	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	protected void updatePosition(AbstractWorkspacePoint pos) {
		position = pos;
		try {
			env.notifyArtifactPositionOrRadiusChange(getId());	
			env.notifyAgentPositionOrRadiusChange(getCreatorId());	
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
			env.notifyAgentPositionOrRadiusChange(this.getCreatorId());	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	/* called directly by the kernel */
	
	void addFocusedArtifact(String wspName, String artName, ArtifactId id){
		defineObsProperty("focused_art",wspName,artName,id);
	}
	void removeFocusedArtifact(String wspName, String artName, ArtifactId id){
		this.removeObsPropertyByTemplate(wspName, artName,null);
	}
	
}
