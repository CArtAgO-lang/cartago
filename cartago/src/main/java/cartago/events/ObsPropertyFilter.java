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

import cartago.IEventFilter;
import cartago.*;

/**
 * A Filter selecting  observable properties
 *  
 * @author aricci
 *
 */
public class ObsPropertyFilter implements IEventFilter {
	
	private String[] properties;
	
	public ObsPropertyFilter(String[] properties){
		this.properties=properties;
	}
	
	public boolean select(ArtifactObsEvent ev){
		ArtifactObsProperty[] added = ev.getAddedProperties();
		ArtifactObsProperty[] changed = ev.getChangedProperties();
		ArtifactObsProperty[] removed = ev.getRemovedProperties();

		if (added != null){
			for (ArtifactObsProperty prop: added){
					String propName = prop.getName();
					for (String name: properties){
						if (name.equals(propName)){
							return true;
						}
					}
				}
			}
			
			if (changed != null){
				for (ArtifactObsProperty prop: changed){
					String propName = prop.getName();
					for (String name: properties){
						if (name.equals(propName)){
							return true;
						}
					}
				}
			}

			if (removed != null){
				for (ArtifactObsProperty prop: removed){
					String propName = prop.getName();
					for (String name: properties){
						if (name.equals(propName)){
							return true;
						}
					}
				}
			}

		return false;
	}
	
}
