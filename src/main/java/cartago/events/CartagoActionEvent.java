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
package cartago.events;

import cartago.CartagoEvent;
import cartago.Op;

/**
 * Base class representing a generic CArtAgO event.
 * 
 * @author aricci
 *
 */
public abstract class CartagoActionEvent extends CartagoEvent {

	private long actionId;
	private Op op;
	
	protected CartagoActionEvent(long id, long actionId, Op op){
		super(id);
		this.actionId = actionId;
		this.op = op;
	}

	/**
	 * Gets the ID of the action
	 * 
	 * @return
	 */
	public long getActionId(){
		return actionId;
	}

	public Op getOp() {
		return op;
	}
}