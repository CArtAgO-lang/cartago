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
 * Keeps track of a pending linked operation in inter-artifact operation execution.
 * 
 * @author aricci
 *
 */
public class PendingOp {

	private boolean hasSucceeded;
	private boolean hasCompleted;
	private java.util.concurrent.locks.ReentrantLock lock;
	private java.util.concurrent.locks.Condition completed;
	private String failure;
	private Tuple failureDesc;
	private long actionId;
	
	public PendingOp(long actionId){
		lock = new java.util.concurrent.locks.ReentrantLock();
		completed = lock.newCondition();
		this.actionId = actionId;
		hasSucceeded = false;
		hasCompleted = false; 
	}

	public long getActionId(){
		return actionId;
	}
	
	public boolean hasCompleted(){
		try {
			lock.lock();
			return hasCompleted;
		} finally {
			lock.unlock();
		}
	}
	
	public boolean hasSucceeded(){
		try {
			lock.lock();
			return hasSucceeded;
		} finally {
			lock.unlock();
		}
	}

	public String getFailure(){
		return failure;
	}
	
	public Tuple getFailureDesc(){
		return failureDesc;
	}
	
	public void waitForCompletion(){
		try {
			lock.lock();
			while (!hasCompleted){
				completed.await();
			}
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void notifyOpFailure(String reason, Tuple desc) {
		try {
			//log("notified failure for "+id);
			lock.lock();
			hasSucceeded = false;
			hasCompleted = true;
			failure = reason;
			failureDesc = desc;
			completed.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public void notifyOpSuccess() {
		try {
			//log("notified success for "+id);
			lock.lock();
			hasSucceeded = true;
			hasCompleted = true;
			completed.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
