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
 * Base class representing a generic CArtAgO event.
 * 
 * @author aricci
 *
 */
public abstract class CartagoEvent implements java.io.Serializable {
	
	private long timestamp;
	private long id;
	
	public CartagoEvent(){}
	
	protected CartagoEvent(long id){
		this.id = id;
		timestamp = System.currentTimeMillis();
	}

	protected CartagoEvent(long id, long ts){
		this.id = id;
		timestamp = ts;
	}

	/**
	 * Gets the ID of the event
	 * 
	 * @return
	 */
	public long getId(){
		return id;
	}

	/**
	 * Gets event time generation
	 * 
	 */
	public long getTimestamp(){
		return timestamp;
	}


}

