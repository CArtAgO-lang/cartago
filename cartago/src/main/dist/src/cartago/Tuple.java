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

import cartago.*;

/**
 * Basic tuple data type.
 * 
 * @author aricci
 *
 */
public class Tuple implements java.io.Serializable {

	private String name;
	private Object[] args;
	private static final Object[] EMPTY = new Object[]{};

	public static final Object ANY = null;
	public static final Object UNKNOWN = null;
    	
	public Tuple(String name){
    	args=EMPTY;
    	this.name = name;
    }

    public Tuple(String name,Object... objs){
    	this.name = name;
    	args=objs;
    }

    /*
    Tuple(String name,Object obj0, Object obj1){
    	this.name = name;
    	args=new Object[]{obj0,obj1};
    }

    Tuple(String name,Object obj0, Object obj1, Object obj2){
    	this.name = name;
    	args=new Object[]{obj0,obj1,obj2};
    }
	*/
    public Tuple(Object[] objs){
    	this.name = (String)objs[0];
    	args=new Object[objs.length-1];
    	for (int i=1; i<objs.length; i++){
    		args[i-1] = objs[i];
    	}
    }
    
    /**
     * Get the label of the tuple.
     * 
     * @return
     */
    public String getLabel(){
    	return name;
    }

    /**
     * Get the i-th argument of the tuple.
     * 
     * @param index index of the argument
     * @return
     */
    public Object getContent(int index){
        return args[index];
    }

    /**
     * Get the full array of arguments.
     * 
     * @return
     */
    public Object[] getContents(){
        return args;
    }

    /**
     * Get the i-th argument coverted to an integer
     * 
     * @param index
     * @return
     */
    public int intContent(int index){
        return ((Number)args[index]).intValue();
    }

    /**
     * Get the i-th argument coverted to an double
     * 
     * @param index
     * @return
     */
    public double doubleContent(int index){
        return ((Number)args[index]).doubleValue();
    }
    
    /**
     * Get the i-th argument coverted to a boolean
     * 
     * @param index
     * @return
     */
    public boolean booleanContent(int index){
        return ((Boolean)args[index]).booleanValue();
    }

    /**
     * Get the i-th argument coverted to a String
     * 
     * @param index
     * @return
     */
    public String stringContent(int index){
        return ((String)args[index]);
    }

    /**
     * Get the arity of the tuple.
     * 
     * @return
     */
    public int getNArgs(){
    	return args.length;
    }
    
    /**
     * Get the string representation of the tuple.
     * 
     * TupleName(TupleArg,...)
     */
    public String toString(){
    	StringBuffer st = new StringBuffer(name);
    	if (args.length>0){
    		st.append("("+args[0]);
    		for (int i=1; i<args.length; i++){
    			st.append(",");
    			String s = args[i].toString();
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
