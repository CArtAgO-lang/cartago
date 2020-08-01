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

import java.util.*;

/**
 * Artifact observable state description
 * 
 * @author aricci
 *
 */
public class ObsStateDescription {

	private String name;
	HashMap<String,Object> values;
	
	public ObsStateDescription(String name, HashMap<String,Object> values){
		this.name = name;
		this.values = values;
	}
	
	public String getObsStateName(){
		return name;
	}
	
	public HashMap<String,Object> getObservableStateProperties(){
		return values;
	}
}
