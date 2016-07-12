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
package cartago.util.agent;

import cartago.*;
import cartago.events.ArtifactObsEvent;

/**
 * Basic class representing a percept
 * 
 * @author aricci
 *
 */
public class Percept implements java.io.Serializable {
	
	private ArtifactObsEvent event;
	
	public Percept(ArtifactObsEvent ev){
			event = ev;
	}	

	public boolean hasSignal(){
		return event.getSignal()!=null;
	}
	
	public Tuple getSignal(){
		return event.getSignal();
	}
	
	public boolean obsPropChanges(){
		return event.getChangedProperties() != null;
	}

	public boolean obsPropAdded(){
		return event.getAddedProperties() != null;
	}
	
	public boolean obsPropRemoved(){
		return event.getRemovedProperties() != null;
	}

	public cartago.ArtifactObsProperty[] getPropChanged(){
		return event.getChangedProperties();
	}
	
	public cartago.ArtifactObsProperty[] getAddedProperties(){
		return event.getAddedProperties();
	}
	
	public cartago.ArtifactObsProperty[] getRemovedProperties(){
		return event.getRemovedProperties();
	}
    
	public cartago.ArtifactId getArtifactSource(){
		if (!hasSignal()){
			return event.getArtifactId();
		} else {
			return null;
		}
	}
	
	public String toString(){
		return "{ signal: "+event.getSignal()+", added_props: "
				+event.getAddedProperties()+", changed_props: "
				+event.getChangedProperties()+", removed_props: "
				+event.getRemovedProperties()+ "}";
	}
}