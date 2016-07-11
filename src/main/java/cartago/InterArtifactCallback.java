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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import cartago.events.ActionSucceededEvent;
import cartago.events.ActionFailedEvent;

public class InterArtifactCallback implements ICartagoCallback {

	private ConcurrentHashMap<Long,PendingOp> pendingOps;
	private AtomicLong opId;
	
	
	public InterArtifactCallback(ReentrantLock lock){
		opId = new AtomicLong(1);
		pendingOps = new ConcurrentHashMap<Long,PendingOp>();
	}
			
	public PendingOp createPendingOp(){
		long id = opId.getAndIncrement();
		PendingOp pa = new PendingOp(id);
		pendingOps.put(id, pa);
		return pa;
	}
	
	//
	
	public void notifyCartagoEvent(CartagoEvent ev){
		if (ev instanceof ActionSucceededEvent){
			ActionSucceededEvent evt = (ActionSucceededEvent)ev;
			PendingOp o = pendingOps.get(evt.getActionId());
			if (o != null){
				o.notifyOpSuccess();
			}
		} else if (ev instanceof ActionFailedEvent){
			ActionFailedEvent evt = (ActionFailedEvent)ev;
			PendingOp o = pendingOps.get(evt.getActionId());
			if (o != null){
				o.notifyOpFailure(evt.getFailureMsg(), evt.getFailureDescr());
			}
		}
	}


}