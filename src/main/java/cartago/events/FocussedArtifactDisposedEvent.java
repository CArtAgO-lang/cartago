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
package cartago.events;

import java.util.List;

import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.CartagoEvent;
import cartago.OpId;
import cartago.Tuple;

/**
 * Class representing an observable event generated when a focussed artifact 
 * has been disposed
 * 
 * @author aricci
 *
 */
public class FocussedArtifactDisposedEvent extends ArtifactObsEvent {
		
	private List<ArtifactObsProperty> props;
	
	FocussedArtifactDisposedEvent(){}
	
	public FocussedArtifactDisposedEvent(long id, ArtifactId src,List<ArtifactObsProperty> props){
		super(id,src,null,null,null,null);
		this.props = props;
	}

	public FocussedArtifactDisposedEvent(long id, ArtifactId src,List<ArtifactObsProperty> props, long ts){
		super(id,src,null,null,null,null, ts);
		this.props = props;
	}

	public List<ArtifactObsProperty> getObsProperties(){
		return props;
	}
	
	public String toString(){
		return "focussed-artifact-disposed-event"+getId()+"-"+this.getArtifactId();
	}

}
