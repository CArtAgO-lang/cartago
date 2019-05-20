package cartago;

import java.util.Optional;

public class WorkspaceDescriptor {

	private Workspace wsp;
	private Optional<WorkspaceDescriptor> parent;
	private boolean isLocal;
	private String protocol;
	private String address;
	private WorkspaceId id;
	private String remotePath;
	
	public WorkspaceDescriptor(WorkspaceId wspId, WorkspaceDescriptor parent) {
		this.parent = Optional.of(parent);
		this.id = wspId;
		this.isLocal = true;
	}

	public WorkspaceDescriptor(WorkspaceId wspId) {
		this.id = wspId;
		this.parent = Optional.empty();
		this.isLocal = true;
	}

	public void setWorkspace(Workspace wsp) {
		this.wsp = wsp;
	}
	
	public void setRemote(String remotePath, String address, String protocol) {
		this.isLocal = false;
		this.protocol = protocol;
		this.address = address;
		this.remotePath = remotePath;
	}
	
	public boolean isLocal() {
		return isLocal;
	}
	
	public WorkspaceId getId() {
		return id;
	}
		
	public String getProtocol() {
		return protocol;
	}
	
	public String getRemotePath() {
		return remotePath;
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
