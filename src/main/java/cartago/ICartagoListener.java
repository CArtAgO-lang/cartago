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
 * User interface to handle CArtAgO Events.
 * 
 * @author aricci
 *
 */
public interface ICartagoListener {
	
	/**
	 * Notify a CArtAgO event occurred inside a workspace.
	 * 
	 * @param ev the event
	 * @return true if the event must enqueued in the percept queue, false if the event can be forgotten 
	 */
	boolean notifyCartagoEvent(CartagoEvent ev);	

}
