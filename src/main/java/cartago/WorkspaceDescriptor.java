package cartago;

import java.util.Optional;
import java.util.UUID;

public class WorkspaceDescriptor {

	private boolean isLocal;
	
	private String envName;
	private UUID envId;
	
	private Workspace wsp;
	private WorkspaceId localWspId;
	private Optional<WorkspaceDescriptor> parent;

	private String protocol;
	private String address;
	private String remoteFullName;
	
	public WorkspaceDescriptor(String envName, UUID envId, WorkspaceId wspId) {
		this.localWspId = wspId;
		this.envName = envName;
		this.envId = envId;
		this.parent = Optional.empty();
		this.isLocal = true;
	}
	
	public WorkspaceDescriptor(String envName, UUID envId, WorkspaceId wspId, WorkspaceDescriptor parent) {
		this(envName, envId, wspId);
		this.parent = Optional.of(parent);
		this.isLocal = true;
	}

	public WorkspaceDescriptor(String envName, UUID envId, WorkspaceId wspId, String remotePath, String address, String protocol) {
		this(envName, envId, wspId);
		this.parent = Optional.empty();
		this.isLocal = false;
		this.address = address;
		this.protocol = protocol;
		this.remoteFullName = remotePath;
	}

	public void setWorkspace(Workspace wsp) {
		this.wsp = wsp;
	}
	
	public void setRemote(String remotePath, String address, String protocol) {
		this.isLocal = false;
		this.protocol = protocol;
		this.address = address;
		this.remoteFullName = remotePath;
	}
	
	public boolean isLocal() {
		return isLocal;
	}
	
	public WorkspaceId getId() {
		return localWspId;
	}
	
	public String getEnvName() {
		return envName;
	}
	
	public UUID getEnvId() {
		return envId;
	}
		
	public String getProtocol() {
		return protocol;
	}
	
	public String getRemotePath() {
		return remoteFullName;
	}
	
	public String getAddress() {
		return address;
	}
	
	public boolean isRoot() {
		return parent.isPresent();
	}
	
	public Workspace getWorkspace() {
		return wsp;
	}
	
	public WorkspaceDescriptor getParentInfo() {
		return parent.get();
	}
	
}
