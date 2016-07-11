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
import java.lang.reflect.*;

/**
 * Default implementation of guards (based on artifact class methods) 
 *
 * @author aricci
 *
 */
public class ArtifactGuardMethod implements IArtifactGuard {

	private Method method;
	private Artifact artifact;
	
	public ArtifactGuardMethod(Artifact artifact, Method method){
		this.method = method;
		this.artifact = artifact;
		method.setAccessible(true);
	}
	
	public boolean eval(Object[] actualParams) throws Exception {
		return (Boolean) method.invoke(artifact, actualParams);
	}

	public int getNumParameters(){
		return method.getParameterTypes().length;
	}

	public String getName(){
		return method.getName();
	}
	
}
