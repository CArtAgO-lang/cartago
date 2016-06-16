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

import cartago.events.ArtifactObsEvent;

/**
 * Basic interface that must be
 * implemented by perception filters
 *  
 * @author aricci
 *
 */
public interface IEventFilter extends java.io.Serializable {

	/**
	 * Checks and selects  a perception 
	 * 
	 * @param p perception
	 * @return true if it is selected
	 */
	boolean select(ArtifactObsEvent p);


}
