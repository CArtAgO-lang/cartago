/**
 * CArtAgO - Developed by aliCE team at deis.unibo.it
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

/**
 * Artifact observable property.
 * 
 * @author aricci
 *
 */
public class ArtifactObsProperty implements java.io.Serializable {

	private String name;
	private Object[] values;
	private ArtifactId aid;
	private long id;
	
	public ArtifactObsProperty(ArtifactId aid, long id, String name, Object... values){
    	this.name = name;
    	this.values = values;
    	this.id = id;
    	this.aid = aid;
	}
	
	/**
	 * Get artifact id
	 * 
	 * @return
	 */
	public ArtifactId getArtifactId(){
		return aid;
	}

	/**
	 * Get obs prop id
	 * 
	 * @return
	 */
	public long getId(){
		return id;
	}
	/**
	 * Get the name of the property
	 * 
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	
	/**
	 * Get a value of the property
	 * @param index index of the value
	 * @return
	 */
	public Object getValue(int index){
		return values[index];
	}
    
	/**
	 * Get the array of values
	 * @return
	 */
	public Object[] getValues(){
		return values;
	}
  
	/**
	 * Update a value
	 * 
	 * @param index index of the value
	 * @param value
	 */
	void updateValue(int index, Object value){
		this.values[index] = value;
	}

	/**
	 * Update all the values
	 * 
	 * @param value
	 */
	void updateValues(Object... values){
		this.values = values;
	}
	
	/**
	 * Get a value of type int of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public int intValue(int index){
		return ((Number)values[index]).intValue();
	}
	
	/**
	 * Get a value of type double of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public double doubleValue(int index){
		return ((Number)values[index]).doubleValue();
	}
	
	/**
	 * Get a value of type string of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public String stringValue(int index){
		return values[index].toString();
	}
	
	/**
	 * Get a value of type boolean of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public boolean booleanValue(int index){
		return (Boolean)values[index];
	}
	
	/**
	 * Get a value of type float of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public float floatValue(int index){
		return ((Number)values[index]).floatValue();
	}
	
	/**
	 * Get a value of type long of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public long longValue(int index){
		return ((Number)values[index]).longValue();
	}
	
	/**
	 * Get a value of type char of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public char charValue(int index){
		return (Character)values[index];
	}
	
	/**
	 * Get the index-0 value of type int of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public int intValue(){
		return ((Number)values[0]).intValue();
	}
	
	/**
	 * Get the index-0 value of type double of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public double doubleValues(){
		return ((Number)values[0]).doubleValue();
	}
	
	/**
	 * Get the index-0 value of type string of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public String stringValue(){
		return values[0].toString();
	}
	
	/**
	 * Get the index-0 value of type boolean of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public boolean booleanValue(){
		return (Boolean)values[0];
	}
	
	/**
	 * Get the index-0 value of type float of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public float floatValue(){
		return ((Number)values[0]).floatValue();
	}
	
	/**
	 * Get the index-0 value of type long of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public long longValue(){
		return ((Number)values[0]).longValue();
	}
	
	/**
	 * Get the index-0 value of type char of the property
	 * 
	 * @param index index of the value
	 * 
	 * @return
	 */
	public char charValue(){
		return (Character)values[0];
	}

	/**
	 * Get the index-0 value
	 * 
	 * 
	 * @param value
	 */
	public Object getValue(){
		return values[0];
	}


	public boolean match(String nam, Object... v){
		if (!name.equals(nam) || values.length != v.length){
			return false;
		} else {
			for (int i = 0; i < values.length; i++){
				if (values[i]!=null && v[i]!=null){
					if (!values[i].equals(v[i])){
						return false;
					}
				}
			}
			return true;
		}
	}
	
	
	public String toString(){
    	StringBuffer st = new StringBuffer(name);
    	if (values.length>0){
    		st.append("("+values[0]);
    		for (int i=1; i<values.length; i++){
    			st.append(",");
    			String s = values[i].toString();
    			if (s.equals("")){
    				s = "\"\"";
    			}
    			st.append(s);	
	    	}
    		st.append(")");
    	}
    	return st.toString();
    }

}
