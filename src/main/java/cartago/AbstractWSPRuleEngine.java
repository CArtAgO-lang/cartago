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

import java.util.concurrent.locks.*;
import cartago.events.ActionSucceededEvent;

/**
 * Base class for implementing WSP Rule engine
 * 
 * @TODO fixing locking for consistency  
 * @TODO extending the primitives
 * 
 * @author aricci
 *
 */
public abstract class AbstractWSPRuleEngine {

	private ReentrantLock lock;
	private Workspace kernel;
	
	protected AbstractWSPRuleEngine(){
		lock = new ReentrantLock();
	}
	
	/**
	 * Process a new operation request
	 * 
	 * The request can inspected or changed or be forced to fail by using 
	 * the OpRequestInfo interface.  If no changes are done (default),
	 * the request is executed as it is.
	 * 
	 * @param desc operation request descriptor
	 */
	protected void processActionRequest(OpRequestInfo desc){
	}

	/**
	 * Process a new request to join by an agent
	 * 
	 * The request can inspected or changed or be forced to fail by using 
	 * the AgentJoinRequestInfo interface. If no changes are done (default),
	 * the request is executed as it is.
	 * 
	 * @param req request info
	 */
	protected void processAgentJoinRequest(AgentJoinRequestInfo req){
	}
	
	/**
	 * Process a new request to join by an agent
	 * 
	 * The request can inspected or changed or be forced to fail by using 
	 * the AgentQuitRequestInfo interface. If no changes are done (default),
	 * the request is executed as it is.
	 * 
	 * @param req request info
	 */
    protected void processAgentQuitRequest(AgentQuitRequestInfo req) {
    }

	// available primitives
	
    /**
     * Execute an operation 
     * 
     * @param aid artifact identifier
     * @param op operation
     */
    final protected boolean execOp(ArtifactId aid, Op op) throws CartagoException  {
		return kernel.wspRuleManExecOp(aid, op);
	}

	/**
	 * Create a new artifact
	 * 
	 * @TODO fix the obs prop addition in WorkspaceArtifact
	 * 
	 * @param artifactName
	 * @param templateName
	 * @param params
	 * @return
	 * @throws CartagoException
	 */
	final protected ArtifactId makeArtifact(String artifactName, String templateName, Object...params) throws CartagoException {
		AgentId id = kernel.getWSPManager().getAgentId();
		return kernel.makeArtifact(id,artifactName,templateName,new ArtifactConfig(params));
	}

	/**
	 * Dispose an existing artifact
	 * 
	 * @param id
	 * @throws CartagoException
	 */
	final protected void disposeArtifact(ArtifactId id) throws CartagoException {
		AgentId aid = kernel.getWSPManager().getAgentId();
		kernel.disposeArtifact(aid, id);
	}
	
	/**
	 * Get the current artifact name list
	 * 
	 * @return
	 */
	final protected String[] getArtifactList(){
		return kernel.getArtifactList();
	}
	
	/**
	 * Get the identifier of an artifact
	 * 
	 * @param name name of the artifact
	 * @return
	 */
	final protected ArtifactId getArtifact(String name){
		return kernel.getArtifact(name);
	}

	/**
	 * Get the value of an artifact observable property
	 * 
	 * @param id artifact identifier
	 * @param propName property name
	 * @return
	 * @throws CartagoException
	 */
	final protected ArtifactObsProperty getArtifactObsProp(ArtifactId id, String propName) throws CartagoException {
		return kernel.wspRuleManReadObsProperty(id, propName);
	}
	
	final protected void lock() throws InterruptedException {
		lock.lock();
	}

	final protected void unlock() throws InterruptedException {
		lock.unlock();
	}
	
	// internals
	
	void setKernel(Workspace kernel){
		this.kernel = kernel;
	}

	protected void processObsPropertyChanged(ArtifactId sourceId, ArtifactObsProperty[] changed){    	
	}

	protected void processObsPropertyAdded(ArtifactId sourceId, ArtifactObsProperty[] added){    	
	}

	protected void processObsPropertyRemoved(ArtifactId sourceId, ArtifactObsProperty[] removed){    	
	}

	protected void processActionCompleted(ActionSucceededEvent ev, AgentId userId ){
	}
	
	protected void processSignal(Tuple signal){
		
	}
	
	/*
	void opRequested(long when, AgentId who, ArtifactId aid, Op op) throws CartagoException;
	void opStarted(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opSuspended(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opResumed(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opCompleted(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opFailed(long when, OpId id, ArtifactId aid, Op op, String msg, Tuple descr)  throws CartagoException;
	void newPercept(long when, ArtifactId aid, Tuple signal, ArtifactObsProperty[] added, ArtifactObsProperty[] removed, ArtifactObsProperty[] changed)  throws CartagoException;
    
	void artifactCreated(long when, ArtifactId id,  AgentId creator)  throws CartagoException;
	void artifactDisposed(long when, ArtifactId id, AgentId disposer)  throws CartagoException;
	void artifactFocussed(long when, AgentId who, ArtifactId id, IEventFilter ev)  throws CartagoException;
	void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id)  throws CartagoException;
	void artifactsLinked(long when, AgentId id, ArtifactId linking, ArtifactId linked)  throws CartagoException;
	
	void agentJoined(long when, AgentId id)  throws CartagoException;
    void agentQuit(long when, AgentId id)  throws CartagoException;
		*/
		
}
