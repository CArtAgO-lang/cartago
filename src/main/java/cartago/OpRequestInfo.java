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

/**
 * Class storing info about an op request
 * 
 * Used by WSP Rule Engine
 * 
 * @author aricci
 *
 */
public class OpRequestInfo {
	
	private long actionId;
	private 	AgentId who;
	private 	Op op;
	private 	boolean failed;
	private 	String failureMsg;
	private 	Tuple  failureDesc;
	private 	ArtifactId id;
	
	OpRequestInfo(long actionId, AgentId who, ArtifactId id, Op op){
		this.actionId = actionId;
		this.who = who;
		this.op = op;
		failed = false;
		this.id = id;
	}
	
	/**
	 * Force the current operation request to fail
	 * 
	 * @param msg message
	 * @param descr reason/description tuple
	 */
	public void setFailed(String msg, Tuple descr){
		failed = true;
		this.failureMsg = msg;
		this.failureDesc = descr;
	}
	
	
	boolean isFailed(){
		return failed;
	}
	
	String getFailureMsg(){
		return failureMsg;
	}
	
	Tuple getFailureDesc(){
		return failureDesc;
	}
	
	/**
	 * Get the current target artifact identifier
	 * 
	 * @return
	 */
	public ArtifactId getTargetArtifactId(){
		return id;
	}
	
	/**
	 * Get the identifier of the agent that executed the operation
	 * 
	 * @return
	 */
	public AgentId getAgentId() {
		return who;
	}

	/**
	 * Get the operation requested
	 * 
	 * @return
	 */
	public Op getOp() {
		return op;
	}

	/**
	 * Change the operation
	 * 
	 * @param op new operation
	 */
	public void changeOp(Op op) {
		this.op = op;
	}

	/**
	 * Get the unique identifier of the action/operation request
	 * @return
	 */
	public long getActionId(){
		return actionId;
	}
}

