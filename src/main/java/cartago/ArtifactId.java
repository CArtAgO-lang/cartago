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

import java.io.*;

/**
 * Identifier of an artifact
 * 
 * @author aricci
 */
public class ArtifactId implements java.io.Serializable {

	private String name;
	private int id;
	private String artifactType;
    private WorkspaceId workspaceId;
	private AgentId creatorId;
	private int hashCode;
		
	ArtifactId(){}
	
	public ArtifactId(String name, int id, String artifactType, WorkspaceId wspId, AgentId creatorId){
		this.name = name;
		this.id = id;
		this.workspaceId = wspId;
		this.creatorId = creatorId;
		this.artifactType = artifactType;
		hashCode = (wspId.getNodeId().getId()+wspId.getName()+id).hashCode();
	}
	
	public String toString(){
		return name;
	}
	
	/**
	 * Get the artifact logic name 
	 * 
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get artifact unique id inside the workspace
	 * 
	 * @return
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Get artifact type (i.e. the name of the template)
	 * 
	 * @return
	 */
	public String getArtifactType(){
		return artifactType;
	}
	/**
	 * Get creator identifier.
	 * 
	 * @return
	 */
	public AgentId getCreatorId(){
		return creatorId;
	}
	
	/**
	 * Check if two ids are equal
	 */
	public boolean equals(Object aid){
		if (aid!=null && (aid instanceof ArtifactId)) {
			ArtifactId wId = (ArtifactId)aid;
			return (wId.getId()==id  
						&& wId.getArtifactType().equals(artifactType)
						&& wId.getCreatorId().equals(creatorId)
						&& wId.getName().equals(name)
						&& wId.getWorkspaceId().equals(workspaceId));
		} else { 
			return false;
		}
	}
	
	/**
	 * Get the identifier of the workspace where the artifact is located.
	 * 
	 * @return
	 */
	public WorkspaceId getWorkspaceId(){
		return workspaceId;
	}
	
	public int hashCode(){
		return hashCode;
	}
}
