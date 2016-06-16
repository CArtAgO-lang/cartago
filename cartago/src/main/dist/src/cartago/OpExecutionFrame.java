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

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * Execution context  of an operation.
 * 
 * @author aricci
 *
 */
class OpExecutionFrame {

	public enum OpExecState { EXECUTING, SUCCEEDED, NOT_ALIGNED, TIME_OUT, INTERRUPTED, OP_NOT_EXIST, FAILED };
	private OpExecState state;
	
	private ICartagoCallback eventListener;
	private long actionId;

	private AgentId userId;
	private OpId oid;
	private Op op;
	private int ncount;
	private long timeout;
	private ArtifactId aid;
	private Thread servingThread;
	
	private WorkspaceKernel kernel;
	
	// for inter-artifact link operation
	private ArtifactId sourceId;
	
	private IAlignmentTest alignmentTest;
	private ReentrantLock lock;
	private Condition opSignaled;
		
	//
	private String failureMsg;
	private Tuple failureReason;
	
	boolean alreadyNotified;
	
	public OpExecutionFrame(WorkspaceKernel kernel, OpId oid, ICartagoCallback ctx, long actionId, AgentId id, ArtifactId aid, Op op, long timeout, IAlignmentTest test){
		this.oid=oid;
		this.op = op;
		this.aid = aid;
		userId = id;
		alignmentTest = test;
		ncount = 0;
		this.timeout = timeout;
		sourceId = null;
		this.actionId = actionId;
		this.eventListener = ctx;
		lock = new ReentrantLock();
		opSignaled = lock.newCondition();
		state = OpExecState.EXECUTING;
		this.kernel = kernel;
		alreadyNotified = false;
	}

	public OpExecutionFrame(WorkspaceKernel kernel, OpId oid, ArtifactId source, ICartagoCallback ctx, long actionId, AgentId id, ArtifactId aid, Op op, long timeout){
		this.oid=oid;
		this.eventListener = ctx;
		this.op = op;
		this.aid = aid;
		userId = id;
		ncount = 0;
		this.timeout = timeout;
		sourceId = source;
		this.actionId = actionId;
		lock = new ReentrantLock();
		opSignaled = lock.newCondition();
		state = OpExecState.EXECUTING;
		this.kernel = kernel;
		alreadyNotified = false;
	}

	public OpExecutionFrame(WorkspaceKernel kernel, OpId oid, ArtifactId aid, Op op){
		this.oid=oid;
		this.op = op;
		this.aid = aid;
		ncount = 0;
		sourceId = null;
		lock = new ReentrantLock();
		opSignaled = lock.newCondition();
		state = OpExecState.EXECUTING;
		this.kernel = kernel;
		alreadyNotified = false;
	}
	
	public OpId getOpId(){
		return oid;
	}

	public AgentId getAgentId(){
		return userId;
	}

	ICartagoCallback getAgentListener(){
		return eventListener;
	}
	
	public ArtifactId getSourceArtifactId(){
		return sourceId;
	}

	public long getActionId(){
		return actionId;
	}
	
	public Op getOperation(){
		return op;
	}
	
	public void setCompletionNotified(){
		alreadyNotified = true;
	}
	
	public boolean completionNotified(){
		return alreadyNotified;
	}
	
	public boolean isInternalOp(){
		return eventListener == null;
	}
	
	public boolean isLinkedOp(){
		return sourceId!=null;
	}

	public ArtifactId getTargetArtifactId(){
		return aid;
	}
	
	public IAlignmentTest getAlignmentTest(){
		return alignmentTest;
	}
	
	public void setFailed(String reason, Tuple descr){
		this.state = OpExecState.FAILED;
		this.failureMsg = reason;
		this.failureReason = descr;
	}

	
	public boolean isFailed(){
		return state == OpExecState.FAILED;
	}

	public boolean isSucceeded(){
		return state == OpExecState.SUCCEEDED;
	}
	
	public String getFailureMsg(){
		return failureMsg;
	}
	
	public Tuple getFailureReason(){
		return failureReason;
	}

	public void setServingThread(Thread t){
		servingThread = t;
	}
	
	public Thread getServingThread(){
		return servingThread;
	}
			
	/**
	 * Called by artifact to notify results.
	 * @return
	 */
	public void notifyOpCompletion() {
		if (actionId != -1){
			kernel.notifyActionCompleted(eventListener, actionId, aid, op, userId);	
		}
	}
		
	/**
	 * Called by the artifact to notify op failure
	 */
	public void notifyOpFailed() {
		if (actionId != -1){
			kernel.notifyActionFailed(eventListener, actionId, op, failureMsg, failureReason);
		}
	}

	/**
	 * Called by the artifact to notify op failure
	 */
	public void notifyOpFailed(String failureMsg, Tuple failureReason) {
		if (actionId != -1){
			kernel.notifyActionFailed(eventListener, actionId, op, failureMsg, failureReason);
		}
	}
	
	//
	
	public int synchLinkingArtifactOk() {
		lock.lock();
		try {
			if (ncount<0){
				// this is the case in which the linking artifact tries
				// to get the synch when a timeout occurred
				return ncount;
			}
			ncount++;
			if (ncount<2){
				try {
					opSignaled.await(timeout,java.util.concurrent.TimeUnit.MILLISECONDS);
					// wait(timeout);
					if (ncount<0){
						return ncount;
					} else if (ncount<2){
						// timeout
						ncount = -1;
					}
				} catch (Exception ex){
					// interruption
					ncount = -3;
				}
				return ncount;
			} else {
				opSignaled.signalAll();
				//notifyAll();
				return 2;
			}
		} finally {
			lock.unlock();
		}
	}

}
