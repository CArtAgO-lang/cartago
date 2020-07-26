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

/**
 * This class represents artifacts starting configuration
 * 
 * @author aricci
 *
 */
public class ArtifactConfig implements java.io.Serializable {

	private Class[] types;
	private Object[] values;
	static private Class[] DEFAULT_CONFIG_TYPE = new Class[0];  
	static private Object[] DEFAULT_CONFIG_VALUE = new Object[0];  
	static public ArtifactConfig DEFAULT_CONFIG = new ArtifactConfig();
	
	public ArtifactConfig(){
		types = DEFAULT_CONFIG_TYPE;
		values = DEFAULT_CONFIG_VALUE;
	}
	
	public ArtifactConfig(Object... args){
		values = args;
		types = new Class[args.length];
		int i = 0;
		for (Object obj: values){
			try {
				types[i++] = obj.getClass();
			} catch (Exception ex){}
		}
	}
	
	public ArtifactConfig(Class[] types, Object[] values){
		this.types=types;
		this.values=values;
	}

	public Class[] getTypes(){
		return types;
	}
	
	public Object[] getValues(){
		return values;
	}

}
