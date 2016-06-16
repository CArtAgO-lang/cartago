package cartago.tools;

import cartago.*;

public class TupleTemplate implements java.io.Serializable {

	private String name;
	private Object[] args;
	private static final Object[] EMPTY = new Object[]{};

	public static final Object ANY = null;
	public static final Object UNKNOWN = null;
    	
	public TupleTemplate(String name){
    	args=EMPTY;
    	this.name = name;
    }

    public TupleTemplate(String name,Object... objs){
    	this.name = name;
    	args=objs;
    	if (args==null){
    		args = EMPTY;
    	}
    }

    public TupleTemplate(Object[] objs){
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
    
    public boolean match(Tuple t){
    	if (!t.getLabel().equals(name) || t.getNArgs()!=args.length){
    		return false;
    	} else {
    		Object[] objs = t.getContents();
    		for (int i = 0; i < objs.length; i++){
    			if (args[i]!=null && !(args[i] instanceof OpFeedbackParam<?>) && objs[i]!=null){
    				if (!args[i].equals(objs[i])){
    					return false;
    				}
    			}
    		}
    		return true;
    	}
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
