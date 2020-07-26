/**
 * CArtAgO - DISI, University of Bologna
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

/**
 * Unique identifier of an operation (instance)
 * executed by an artifact
 * 
 * @author aricci
 *
 */
public class OpId implements java.io.Serializable {

	private int id;
	private ArtifactId aid;
	private AgentId agentId;
    private String opName;
    
    OpId(ArtifactId aid, String opName, int id, AgentId ctxId){
		this.id = id;
		this.aid = aid;
		this.agentId = ctxId;
		this.opName = opName;
	}
	    
	/**
	 * Get the numeric identifier of the operation id
	 * 
	 * @return
	 */
    public int getId(){
		return id;
	}

    /**
     * Get the operation name.
     * 
     * @return
     */
    public String getOpName(){
    	return opName;
    }
    
    /**
     * Get the id of the artifact where the operation has been executed
     * 
     * @return
     */
	public ArtifactId getArtifactId(){
		return aid;
	}

	/**
	 * Get the identifier of the agent performer of the operation
	 * 
	 * @return
	 */
	public AgentId getAgentBodyId(){
		return agentId;
	}
	
	
	AgentId getContextId(){
		return agentId;
	}
	
	public boolean equals(Object obj){
		return aid.equals(((OpId)obj).aid) &&  ((OpId)obj).id==id;
	}
	
	public String toString(){
		return "opId("+id+","+opName+","+aid+","+agentId+")";
	}

}
