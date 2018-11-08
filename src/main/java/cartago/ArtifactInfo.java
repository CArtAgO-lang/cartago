package cartago;

import java.util.*;

/**
 * Information about an artifact of the workspace.
 * @author aricci
 *
 */
public class ArtifactInfo implements java.io.Serializable {

	private AgentId creatorId;
	private ArtifactId id;
    private List<OpDescriptor> operations;
    private List<ArtifactObsProperty> obsProperties;
    private List<OperationInfo> ongoingOp;
    private List<ArtifactObserver> observers;
    private Manual manual;
    
	public ArtifactInfo(AgentId creatorId, ArtifactId id,
			List<OpDescriptor> operations,
			List<ArtifactObsProperty> obsProperties,
			List<OperationInfo> ongoingOp,
			Manual manual) {
		super();
		this.creatorId = creatorId;
		this.id = id;
		this.operations = operations;
		this.obsProperties = obsProperties;
		this.ongoingOp = ongoingOp;
		this.manual = manual;
	}  
	
	public ArtifactInfo(AgentId creatorId, ArtifactId id,
			List<OpDescriptor> operations,
			List<ArtifactObsProperty> obsProperties,
			List<OperationInfo> ongoingOp,
			List<ArtifactObserver> observers,
			Manual manual) {
		super();
		this.creatorId = creatorId;
		this.id = id;
		this.operations = operations;
		this.obsProperties = obsProperties;
		this.ongoingOp = ongoingOp;
		this.observers = observers;
		this.manual = manual;
	}  

	public AgentId getCreatorId() {
		return creatorId;
	}

	public ArtifactId getId() {
		return id;
	}

	public List<OpDescriptor> getOperations() {
		return operations;
	}

	public List<ArtifactObsProperty> getObsProperties() {
		return obsProperties;
	}

	public List<OperationInfo> getOngoingOp(){
		return ongoingOp;
	}
	
	/**
	 * Return the list of agents that are observing this artifact
	 * @return list o agents that are observing this artifact
	 */
	public List<ArtifactObserver> getObservers() {
		return observers;
	}
	
	public Manual getManual(){
		return manual;
	}
}
