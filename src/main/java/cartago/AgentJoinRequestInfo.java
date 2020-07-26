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
 * Class storing info about an agent join request
 * 
 * Used by WSP Rule Engine
 * 
 * @author aricci
 *
 */
public class AgentJoinRequestInfo {
	
	private 	boolean failed;
	private 	String failureMsg;
	private 	AgentId agentId;
	
	AgentJoinRequestInfo(AgentId agentId){
		failed = false;
		this.agentId = agentId;
	}
	
	/**
	 * Force the request to fail
	 * 
	 * @param msg message
	 * @param descr reason/description tuple
	 */
	public void setFailed(String msg){
		failed = true;
		this.failureMsg = msg;
	}
	
	boolean isFailed(){
		return failed;
	}
	
	String getFailureMsg(){
		return failureMsg;
	}
	
	/**
	 * Get the agent that wants to join
	 * @return
	 */
	public AgentId getAgentId() {
		return agentId;
	}
}

