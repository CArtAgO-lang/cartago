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

import java.util.List;

import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.CartagoEvent;
import cartago.ObservableArtifactInfo;
import cartago.OpId;
import cartago.Tuple;

/**
 * EXPERIMENTAL - Topology support
 * 
 * Class representing an observable artifact list changed event,
 * @author aricci
 *
 */
public class ObsArtListChangedEvent extends CartagoEvent {
	
	private List<ObservableArtifactInfo> newFocused;
	private List<ObservableArtifactInfo> noMoreFocused;
	
	public ObsArtListChangedEvent(long id, List<ObservableArtifactInfo> newFocused, List<ObservableArtifactInfo> noMoreFocused){
		super(id);
		this.newFocused = newFocused;
		this.noMoreFocused = noMoreFocused;
	}
	
	
	public String toString(){
		return "obs-art-list-changed "+getId();
		//return signal.toString();
	}


	public List<ObservableArtifactInfo> getNewFocused() {
		return newFocused;
	}


	public List<ObservableArtifactInfo> getNoMoreFocused() {
		return noMoreFocused;
	}

}
