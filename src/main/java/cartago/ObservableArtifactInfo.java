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

import cartago.*;
import java.util.*;

/**
 * TOPOLOGY EXTENSION:
 * Class storing info about an observable artifact 
 * 
 * @author aricci
 *
 */
public class ObservableArtifactInfo implements java.io.Serializable {

	private ArtifactId targetArtifact;
	private List<ArtifactObsProperty> props;
	private transient ArtifactDescriptor descriptor; // valid only on kernel side
	
	public ObservableArtifactInfo(ArtifactDescriptor des, List<ArtifactObsProperty> props){
		this.targetArtifact = des.getArtifact().getId();
		this.props = props;
		this.descriptor = des;
	}

	public ObservableArtifactInfo(ArtifactId targetArtifact,List<ArtifactObsProperty> props){
		this.targetArtifact = targetArtifact;
		this.props = props;
	}
	
	public ArtifactId getTargetArtifact(){
		return targetArtifact;
	}
	
	public List<ArtifactObsProperty> getObsProperties(){
		return props;
	}

	public ArtifactDescriptor getDescriptor(){
		return descriptor;
	}
	
	
}
