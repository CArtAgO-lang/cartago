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
import java.lang.reflect.*;

/**
 * Dynamic operations (based on functions defined by the user) 
 * 
 * @author aricci
 *
 */
public class ArtifactDynOp implements IArtifactOp {

	private ArtifactDynOpInterface proc;
	private String opName;
	private Artifact artifact;
	private int numParameters;
	
	public ArtifactDynOp(Artifact artifact, String opName, ArtifactDynOpInterface proc){
		this.proc = proc;
		this.opName = opName;
		this.artifact = artifact;
		try {
			Method m = proc.getClass().getMethod("apply", Artifact.class, Object[].class);
			this.numParameters = m.getParameterCount();
		} catch (Exception ex) {
			this.numParameters = -1;
		}
	}
	
	public void exec(Object[] actualParams) throws Exception {
		proc.apply(artifact, actualParams);
	}

	public int getNumParameters(){
		return this.numParameters;
	}

	public String getName(){
		return opName;
	}
	
	public boolean isVarArgs(){
		return false;
	}
}
