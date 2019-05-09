package cartago;

public class AgentBodyArtifact extends Artifact {
	
	/* defines the radius within which the agent can perceive artifacts*/
	protected double observingRadius;

	private AgentBody body;
	
	@OPERATION void init(AgentBody body){
		this.body = body;
		defineObsProperty("joined",body.getWorkspaceId().getName(),body.getWorkspaceId()); 
	}
	
	
	protected void setupPosition(AbstractWorkspacePoint pos, double observabilityRadius, double observingRadius){
		position = pos;
		this.observabilityRadius = observabilityRadius;
		this.observingRadius = observingRadius;
		kernel.bindAgentBodyArtifact(this.getCreatorId(),this);
		try {
			kernel.notifyArtifactPositionOrRadiusChange(this.getId());	
			kernel.notifyAgentPositionOrRadiusChange(getCreatorId());	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	protected void updatePosition(AbstractWorkspacePoint pos) {
		position = pos;
		try {
			kernel.notifyArtifactPositionOrRadiusChange(getId());	
			kernel.notifyAgentPositionOrRadiusChange(getCreatorId());	
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
			kernel.notifyAgentPositionOrRadiusChange(this.getCreatorId());	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	/* called directly by the kernel */
	
	void addFocusedArtifact(String wspName, String artName, ArtifactId id){
		defineObsProperty("focused",wspName,artName,id);
	}
	
	void removeFocusedArtifact(String wspName, String artName, ArtifactId id){
		this.removeObsPropertyByTemplate("focused", wspName, artName,null);
	}
	
}
