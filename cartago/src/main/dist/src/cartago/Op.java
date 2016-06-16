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

/**
 * Basic class representing operations to be executed on artifacts
 * 
 * @author aricci
 *
 */
public class Op implements java.io.Serializable {

	private String name;
	private Object[] values;
		
	/**
	 * Specifies an operation with any number of arguments
	 * 
	 * @param targetId id of the artifact
	 * @param name operation name
	 */
	public Op(String name, Object... params){
		this.name = name;
		values = params;
	}

	public String getName(){
		return name;
	}
	
	public Object[] getParamValues(){
		return values;
	}


	public String toString(){
		StringBuffer st = new StringBuffer("( "+name);
		for (Object v: values){
			st.append(" "+v);
		}
		st.append(" )");
		return st.toString();
	}
}
