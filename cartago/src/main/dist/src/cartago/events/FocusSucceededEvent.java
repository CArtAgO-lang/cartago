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

import cartago.*;
import java.util.*;
/**
 * Class representing an op completed  event.
 * 
 * @author aricci
 *
 */
public class FocusSucceededEvent extends ActionSucceededEvent {

	private ArtifactId targetArtifact;
	private List<ArtifactObsProperty> props;
	
	public FocusSucceededEvent(long id, long actionId, Op op, ArtifactId aid, ArtifactId targetArtifact, List<ArtifactObsProperty> props){
		super(id,actionId,op,aid);
		this.targetArtifact = targetArtifact;
		this.props = props;
	}
	
	public ArtifactId getTargetArtifact(){
		return targetArtifact;
	}
	
	public List<ArtifactObsProperty> getObsProperties(){
		return props;
	}

}
