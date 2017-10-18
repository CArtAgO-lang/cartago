package jaca;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

import jaca.ToProlog;
import cartago.*;


import jason.JasonException;
import jason.NoValueException;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;

/**
 * Library for managing Java objects inside a Jason agent.
 * 
 * @author aricci
 *
 */
public class JavaLibrary {

    private HashMap<String,Object> currentObjects;
    private HashMap<Object,Atom> currentObjects_inverse;
    private long id;
    
    
    public JavaLibrary(){		
        currentObjects = new HashMap<String,Object>();
        currentObjects_inverse = new HashMap<Object,Atom>();
        id = 0;
	}

    /**
     * Creates of a java object 
     */
    public boolean java_new_object(Unifier un, Term className, ListTerm arg, Term id) throws JasonException {
            if (!className.isString()) {
            	throw new JasonException("Invalid class name " + className);
            }
            String clName = ((StringTerm) className).getString();
            // check for array type
            /*
            if (clName.endsWith("[]")) {
                Object[] list = getArrayFromList(arg);
                int nargs = ((Number) list[0]).intValue();
                return java_array(clName, nargs, id);
            }*/
            Signature args = parseArg(getArrayFromList(arg));
            if (args == null) {
            	throw new JasonException("Invalid signature ");
            }
            // object creation with argument described in args
            try {
                Class cl = Class.forName(clName);
                Object[] args_value = args.getValues();
                //
                //Constructor co=cl.getConstructor(args.getTypes());
                Constructor co = lookupConstructor(cl, args.getTypes(), args_value);
                //
                //
                if (co==null){
            		System.out.println("NO CONSTRUCTOR FOUND FOR "+clName);
                	for (int i = 0; i < args_value.length; i++){
                		System.out.println("Param: "+args_value[i]+" type "+args.getTypes()[i]);
                	}
                	throw new JasonException("Constructor not found: class " + clName);
                }

                Object obj = co.newInstance(args_value);
                return bindObject(un, id, obj);
            } catch (ClassNotFoundException ex) {
                throw new JasonException("Java class not found: " + clName);
            } catch (InvocationTargetException ex) {
            	throw new JasonException("Invalid constructor arguments.");
            } catch (NoSuchMethodException ex) {
            	throw new JasonException("Constructor not found: " + args.getTypes());
            } catch (InstantiationException ex) {
            	throw new JasonException("Objects of class " + clName + " cannot be instantiated");
            } catch (IllegalArgumentException ex) {
            	throw new JasonException("Illegal constructor arguments  " + args);
            } catch (IllegalAccessException ex) {
            	throw new JasonException("Illegal access exception");
            }
    }

    /**
     * Creates of a java object 
     */
    public Object newObject(Unifier un, Term className, ListTerm arg) throws JasonException {
            if (!className.isString()) {
            	throw new JasonException("Invalid class name " + className);
            }
            String clName = ((StringTerm) className).getString();
            // check for array type
            /*
            if (clName.endsWith("[]")) {
                Object[] list = getArrayFromList(arg);
                int nargs = ((Number) list[0]).intValue();
                return java_array(clName, nargs, id);
            }*/
            Signature args = parseArg(getArrayFromList(arg));
            if (args == null) {
            	throw new JasonException("Invalid signature ");
            }
            // object creation with argument described in args
            try {
                Class cl = Class.forName(clName);
                Object[] args_value = args.getValues();
                //
                //Constructor co=cl.getConstructor(args.getTypes());
                Constructor co = lookupConstructor(cl, args.getTypes(), args_value);
                //
                //
                if (co==null){
            		System.out.println("NO CONSTRUCTOR FOUND FOR "+clName);
                	for (int i = 0; i < args_value.length; i++){
                		System.out.println("Param: "+args_value[i]+" type "+args.getTypes()[i]);
                	}
                	throw new JasonException("Constructor not found: class " + clName);
                }

                return  co.newInstance(args_value);
            } catch (ClassNotFoundException ex) {
                throw new JasonException("Java class not found: " + clName);
            } catch (InvocationTargetException ex) {
            	throw new JasonException("Invalid constructor arguments.");
            } catch (NoSuchMethodException ex) {
            	throw new JasonException("Constructor not found: " + args.getTypes());
            } catch (InstantiationException ex) {
            	throw new JasonException("Objects of class " + clName + " cannot be instantiated");
            } catch (IllegalArgumentException ex) {
            	throw new JasonException("Illegal constructor arguments  " + args);
            } catch (IllegalAccessException ex) {
            	throw new JasonException("Illegal access exception");
            }
    }

    /**
     * Creates of a java array 
     */
    public boolean java_new_array(Unifier un, Term className, ListTerm elems, Term id) throws JasonException {
            if (!className.isString()) {
            	throw new JasonException("Invalid class name " + className);
            }
            String arrayType = ((StringTerm) className).getString();
            // check for array type
            Object[] list = getArrayFromList(elems);
            int nargs = list.length;
            Object newArray = null;
            try {
	            String obtype = arrayType.substring(0, arrayType.length() - 2);
	            if (obtype.equals("boolean")) {
	                boolean[] array = new boolean[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Boolean)list[i];
	                }
	                newArray = array;
	            } else if (obtype.equals("byte")) {
	                byte[] array = new byte[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Byte)list[i];
	                }
	                newArray = array;
	            } else if (obtype.equals("char")) {
	                char[] array = new char[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Character)list[i];
	                }
	                newArray = array;
	            } else if (obtype.equals("short")) {
	                short[] array = new short[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Short)list[i];
	                }
	                newArray = array;
	            } else if (obtype.equals("int")) {
	                int[] array = new int[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Integer)list[i];
	                }
	                newArray = array;
	            } else if (obtype.equals("long")) {
	                long[] array = new long[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Long)list[i];
	                }
	                newArray = array;
	            } else if (obtype.equals("float")) {
	                float[] array = new float[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Float)list[i];
	                }
	                newArray = array;
	            } else if (obtype.equals("double")) {
	                double[] array = new double[nargs];
	                for (int i = 0; i < array.length; i++){
	                	array[i] = (Double)list[i];
	                }
	                newArray = array;
	            } else {
	                Class cl = Class.forName(obtype);
	                newArray = Array.newInstance(cl, nargs);
	                for (int i = 0; i < list.length; i++){
	                	Array.set(newArray,i,termToObject((Term)list[i]));
	                }
	            }	            
                return bindObject(un, id, newArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        	throw new JasonException("Java array creation failed  "+className);
        }
    }    
    /**
    *
    * Calls a method of a Java object
    *
    */
   public boolean java_call(Unifier un, Term objId, Term method, Term idResult) throws JasonException  {
       Object obj = null;
       Signature args = null;
       String methodName = null;
       String forceClassName = null;
       
       try {
           if (method.isAtom()){
        	   methodName = ((Atom)method).getFunctor();
           } else {
        	   methodName = ((Structure)method).getFunctor();
           }
           // check for accessing field   Obj.Field <- set/get(X)
           //  in that case: objId is '.'(Obj, Field)
           //System.out.println("calling "+methodName);
           args = parseArg(method);

           // object and argument must be instantiated
           if (args == null)
               return false;

           Object res = null;
           //System.out.println(args);
           if (objId.isAtom()){
        	   String objName = ((Atom)objId).getFunctor();
        	   synchronized (currentObjects){
        	     obj = currentObjects.get(objName);
        	   }
	           if (obj != null) {
	           		Class cl = null;
	           		if (forceClassName==null){
	           			cl = obj.getClass();
	           		} else {
	           			cl = Class.forName(forceClassName);
	           		}

	               Object[] args_values = args.getValues();
	               Method m = lookupMethod(cl, methodName, args.getTypes(), args_values);
	               //
	               //
	               if (m != null) {
	                   try {
	                       // works only with JDK 1.2, NOT in Sun Application Server!
	                       //m.setAccessible(true);
	                       res = m.invoke(obj, args_values);
	                       //System.out.println("invoked "+res);
	                   } catch (IllegalAccessException ex) {
	                       throw new JasonException("Method invocation failed: " + methodName+ "( signature: " + args + " )");
	                   }
	               } else {
	            	   throw new JasonException("Method not found: " + methodName+ "( signature: " + args + " )");
	               }
	           } else {
            	   throw new JasonException("Method not found: " + methodName+ "( signature: " + args + " )");
	           }
           } else if (objId.isString()) {
                       try {
                           Class cl = Class.forName(((jason.asSyntax.StringTerm)objId).getString());
                           Method m = cl.getMethod(methodName, args.getTypes());
                           //m.setAccessible(true);
                           res = m.invoke(null, args.getValues());
                       } catch (ClassNotFoundException ex) {
                           // if not found even as a class id -> consider as a String object value
                    	   throw new JasonException("Class not found: " + objId);
                       }                   
           } else {
        	   throw new JasonException("Invalid target: " + objId);
           }
           if (idResult!=null){
        	   return parseResult(un, idResult, res);
           } else {
        	   return true;
           }
       } catch (InvocationTargetException ex) {
    	   ex.printStackTrace();
           throw new JasonException("Method failed: " + methodName + " - ( signature: " + args +
                       " ) - Original Exception: "+ex.getTargetException());
       } catch (NoSuchMethodException ex) {
           throw new JasonException("Method not found: " + methodName+ " - ( signature: " + args + " )");
       } catch (IllegalArgumentException ex) {
    	   throw new JasonException("Invalid arguments " + args+ " - ( method: " + methodName + " )");
       } catch (Exception ex) {
    	   throw new JasonException("Generic error in method invocation " + methodName);
       }
   }
   
   public boolean javaArrayToList(Unifier un, Atom objId, Term idResult) throws JasonException  {
	 try {
		 String objName = objId.getFunctor();
		 Object obj = null;
		 synchronized (currentObjects){
      	   obj = currentObjects.get(objName);
		 }
      	 ListTermImpl list = new ListTermImpl();
	     if (obj.getClass().isArray()){
	    	 int nlen = Array.getLength(obj);
	    	 for (int i = 0; i < nlen; i++){
	    		 Atom id = this.registerDynamic(Array.getInt(obj,i));
	    		 list.add(id);
	    	 }
	     } else if (obj instanceof Collection) {
	    	 for (Object el: (Collection)obj){
	    		 Atom id = this.registerDynamic(el);
	    		 list.add(id);
	    	 }
	     }
         return un.unifies(idResult,list);
	 } catch (Exception ex){
		 throw new JasonException("Error in arrat_to_list for "+objId);
	 }
   }

   public   Object getObject(Atom id){
	   // System.out.println("LIB: GEtting object "+id.getFunctor()+" in "+this.toString());
	   synchronized (currentObjects){
		   return currentObjects.get(id.getFunctor());
	   }
	}
   
   public Collection<Object> getAllCurrentObjects() {
	   return currentObjects.values();
   }
    
    //------
    
    
    /**
     * Tries to bind specified id to a provided java object.
     * 
     * Term id can be a variable or a ground term.
     */
    public boolean bindObject(Unifier un, Term id, Object obj) {
        // null object are considered to _ variable
        if (obj == null) {
            return un.unifies(id, new jason.asSyntax.VarTerm("_"));
        }
        // already registered object?
        synchronized (currentObjects){
            Term aKey = currentObjects_inverse.get(obj);
            if (aKey != null) {
                // object already referenced -> unifying terms
                // referencing the object
                //log("obj already registered: unify "+id+" "+aKey);
                return un.unifies(id, (Term) aKey);
            } else {
                // object not previously referenced
                if (id.isVar()) {
                    // get a ground term
                    Atom idTerm = generateFreshId();
                    un.unifies(id, idTerm);
                    registerDynamic(idTerm, obj);
                    //log("not ground id for a new obj: "+id+" as ref for "+obj);
                    return true;
                } else {
                    // verify of the id is already used
                	Atom id2 = (Atom)id;
                    Object linkedobj = currentObjects.get(id2.getFunctor());
                    if (linkedobj == null) {
                        registerDynamic(id2, obj);
                        //log("ground id for a new obj: "+id+" as ref for "+obj);
                        return true;
                    } else {
                        // an object with the same id is already
                        // present: must be the same object
                        return obj == linkedobj;
                    }
                }
            }
        }
    }


    /**
     * Generates a fresh numeric identifier
     * @return
     */
    protected Atom generateFreshId() {
    		return new Atom("cobj_" + id++);        
    }

	/**
	 * Registers an object only for the running query life-time
	 * 
	 * @param id object identifier
	 * @param obj object 
	 */
	public void registerDynamic(Atom id, Object obj) {
	    synchronized (currentObjects){
	        currentObjects.put(id.getFunctor(), obj);
	        currentObjects_inverse.put(obj, id);
	    }
	}

	/**
	 * Registers an object for the query life-time, 
	 * with the automatic generation of the identifier.
	 * 
	 * If the object is already registered,
	 * its identifier is returned
	 * 
	 * @param obj object to be registered
	 * @return identifier
	 */
	public   Atom registerDynamic(Object obj) {
        //System.out.println("lib: "+this+" current id: "+this.id);
        // already registered object?
        synchronized (currentObjects){
            Atom aKey = currentObjects_inverse.get(obj);
            if (aKey != null) {
                // object already referenced -> unifying terms
                // referencing the object
                // System.out.println("LIB registering "+obj+" - obj already registered: unify "+id+" "+aKey);
                return aKey;
            } else {
            	Atom id = generateFreshId();
                currentObjects.put(id.getFunctor(), obj);
                currentObjects_inverse.put(obj, id);
                // System.out.println("LIB registering "+obj+" - id "+id.getFunctor()+" in "+this.toString());
                return id;
            }
        }
    }
    	
	// --------------------------------------------------
	
    /**
     * creation of method signature from prolog data
     */
    private Signature parseArg(Term meth) {
    	if (meth.isStructure()){
    		Structure method = (Structure)meth;
	        Object[] values = new Object[method.getArity()];
	        Class[] types = new Class[method.getArity()];
	        for (int i = 0; i < method.getArity(); i++) {
	            if (!parse_arg(values, types, i, (Term) method.getTerm(i)))
	                return null;
	        }
	        return new Signature(values, types);
    	} else if (meth.isAtom()){
    		Atom method = (Atom)meth;
	        Object[] values = new Object[0];
	        Class[] types = new Class[0];
	        return new Signature(values, types);
    	} else {
    		return null;
    	}
    }

    private Signature parseArg(Object[] objs) {
        Object[] values = new Object[objs.length];
        Class[] types = new Class[objs.length];
        for (int i = 0; i < objs.length; i++) {
            if (!parse_arg(values, types, i, (Term) objs[i]))
                return null;
        }
        return new Signature(values, types);
    }

    private boolean parse_arg(Object[] values, Class[] types, int i, Term term) {
        try {
    	    if (term == null) {
                values[i] = null;
                types[i] = null;
            } else if (term.isString()) {
            	types[i] = String.class;
            	values[i] = ((StringTerm)term).getString();
            } else if (term.isAtom()) {
                String name = ((Atom)term).getFunctor();
                if (name.equals("true")){
                	values[i]=Boolean.TRUE;
					types[i] = Boolean.TYPE;
                } else if (name.equals("false")){
					values[i]=Boolean.FALSE;
					types[i] = Boolean.TYPE;
                } else {
                	Object obj = null;
                	synchronized (currentObjects){
	                 obj = currentObjects.get(name);
                	}
	                if (obj == null) {
	                    values[i] = name;
	                } else {
	                    values[i] = obj;
	                }
					types[i] = values[i].getClass();
                }
            } else if (term.isNumeric()) {
                NumberTerm t = (NumberTerm) term;
                double value = t.solve();
                int intValue = (int)value;
                if (value == intValue){
                    values[i] = new java.lang.Integer(intValue);
                    types[i] = java.lang.Integer.TYPE;
                } else {
                    values[i] = new java.lang.Double(value);
                    types[i] = java.lang.Double.TYPE;
                }
            } else if (term.isList()){
            	ListTerm lt = (ListTerm)term;
    			int j = 0;
    			Object[] list = new Object[lt.size()];
    			for (Term t1: lt){
    				list[j++] = termToObject(t1);
    			}
    			values[i] = list;
    			types[i] = list.getClass();
    			return true;
    	    } else if (term.isStructure()) {
                // argument descriptors
                Structure tc = (Structure) term;
                if (tc.getFunctor().equals("as")) {
                    return parse_as(values, types, i, tc.getTerm(0), tc.getTerm(1));
                } else {
                    Object obj = currentObjects.get(tc.getFunctor());
                    if (obj == null) {
                        values[i] = tc.toString();
                    } else {
                        values[i] = obj;
                    }
                    types[i] = values[i].getClass();
                }
            } else if (term.isVar() && !((VarTerm)term).isGround()) {
                values[i] = null;
                types[i] = Object.class;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     *
     * parsing 'as' operator, which makes it possible
     * to define the specific class of an argument
     *
     */
    private boolean parse_as(Object[] values, Class[] types, int i, Term castWhat, Term castTo) {
        try {
            if (!castWhat.isNumeric()) {
                String castTo_name = ((StringTerm) castTo).getString();
                String castWhat_name = ((StringTerm) castTo).getString();
                //System.out.println(castWhat_name+" "+castTo_name);
                if (castTo_name.equals("java.lang.String") && 
                	castWhat_name.equals("true")){
                	values[i]="true";
                	types[i]=String.class;	
                	return true;
               	} else if (castTo_name.equals("java.lang.String") && 
									castWhat_name.equals("false")){
					values[i]="false";
					types[i]=String.class;	
					return true;
				} else if (castTo_name.endsWith("[]")) {
                    if (castTo_name.equals("boolean[]")) {
                        castTo_name = "[Z";
                    } else if (castTo_name.equals("byte[]")) {
                        castTo_name = "[B";
                    } else if (castTo_name.equals("short[]")) {
                        castTo_name = "[S";
                    } else if (castTo_name.equals("char[]")) {
                        castTo_name = "[C";
                    } else if (castTo_name.equals("int[]")) {
                        castTo_name = "[I";
                    } else if (castTo_name.equals("long[]")) {
                        castTo_name = "[L";
                    } else if (castTo_name.equals("float[]")) {
                        castTo_name = "[F";
                    } else if (castTo_name.equals("double[]")) {
                        castTo_name = "[D";
                    } else {
                        castTo_name = "[L" + castTo_name.substring(0, castTo_name.length() - 2) + ";";
                    }
                }
                if (!castWhat_name.equals("null")) {
                	Object obj_to_cast = null;
                	synchronized (currentObjects){
                       obj_to_cast = currentObjects.get(castWhat_name);
                	}
                    if (obj_to_cast == null) {
                        if (castTo_name.equals("boolean")) {
                            if (castWhat_name.equals("true")) {
                                values[i] = new Boolean(true);
                            } else if (castWhat_name.equals("false")) {
                                values[i] = new Boolean(false);
                            } else {
                                return false;
                            }
                            types[i] = Boolean.TYPE;
                        } else {
                            // conversion to array
                            return false;
                        }
                    } else {
                        values[i] = obj_to_cast;
                        try {
                            types[i] = (Class.forName(castTo_name));
                        } catch (ClassNotFoundException ex) {
                            return false;
                        }
                    }
                } else {
                    values[i] = null;
                    if (castTo_name.equals("byte")) {
                        types[i] = Byte.TYPE;
                    } else if (castTo_name.equals("short")) {
                        types[i] = Short.TYPE;
                    } else if (castTo_name.equals("char")) {
                        types[i] = Character.TYPE;
                    } else if (castTo_name.equals("int")) {
                        types[i] = java.lang.Integer.TYPE;
                    } else if (castTo_name.equals("long")) {
                        types[i] = java.lang.Long.TYPE;
                    } else if (castTo_name.equals("float")) {
                        types[i] = java.lang.Float.TYPE;
                    } else if (castTo_name.equals("double")) {
                        types[i] = java.lang.Double.TYPE;
                    } else if (castTo_name.equals("boolean")) {
                        types[i] = java.lang.Boolean.TYPE;
                    } else {
                        try {
                            types[i] = (Class.forName(castTo_name));
                        } catch (ClassNotFoundException ex) {
                            return false;
                        }
                    }
                }
            } else {
                Number num = (Number) castWhat;
                String castTo_name = ((StringTerm) castTo).getString();
                if (castTo_name.equals("byte")) {
                    values[i] = new Byte((byte) num.intValue());
                    types[i] = Byte.TYPE;
                } else if (castTo_name.equals("short")) {
                    values[i] = new Short((short) num.intValue());
                    types[i] = Short.TYPE;
                } else if (castTo_name.equals("int")) {
                    values[i] = new Integer(num.intValue());
                    types[i] = Integer.TYPE;
                } else if (castTo_name.equals("long")) {
                    values[i] = new java.lang.Long(num.longValue());
                    types[i] = java.lang.Long.TYPE;
                } else if (castTo_name.equals("float")) {
                    values[i] = new java.lang.Float(num.floatValue());
                    types[i] = java.lang.Float.TYPE;
                } else if (castTo_name.equals("double")) {
                    values[i] = new java.lang.Double(num.doubleValue());
                    types[i] = java.lang.Double.TYPE;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }


    /**
     *  parses return value
     *  of a method invokation
     */
    private boolean parseResult(Unifier un, Term id, Object obj) {
        if (obj == null) {
            //return unify(id,Term.TRUE);
            return un.unifies(id, new VarTerm("_"));
        }
        try {
            if (Boolean.class.isInstance(obj)) {
                if (((Boolean) obj).booleanValue()) {
                    return un.unifies(id, Atom.LTrue);
                } else {
                    return un.unifies(id, Atom.LFalse);
                }
            } else if (Byte.class.isInstance(obj)) {
                return un.unifies(id, new jason.asSyntax.NumberTermImpl(((Byte) obj).intValue()));
            } else if (Short.class.isInstance(obj)) {
                return un.unifies(id, new jason.asSyntax.NumberTermImpl(((Short) obj).intValue()));
            } else if (Integer.class.isInstance(obj)) {
                return un.unifies(id, new jason.asSyntax.NumberTermImpl(((Integer) obj).intValue()));
            } else if (java.lang.Long.class.isInstance(obj)) {
                return un.unifies(id, new jason.asSyntax.NumberTermImpl(((java.lang.Long) obj).longValue()));
            } else if (java.lang.Float.class.isInstance(obj)) {
                return un.unifies(id, new jason.asSyntax.NumberTermImpl(((java.lang.Float) obj).floatValue()));
            } else if (java.lang.Double.class.isInstance(obj)) {
                return un.unifies(id, new jason.asSyntax.NumberTermImpl(((java.lang.Double) obj).doubleValue()));
            } else if (String.class.isInstance(obj)) {
                return un.unifies(id, new StringTermImpl((String) obj));
            } else if (Character.class.isInstance(obj)) {
                return un.unifies(id, new StringTermImpl(((Character) obj).toString()));
            } else {
                return bindObject(un, id, obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Object[] getArrayFromList(ListTerm list) {
        Object args[] = new Object[list.size()];
        Iterator it = list.listIterator();
        int count = 0;
        while (it.hasNext()) {
            args[count++] = it.next();
        }
        return args;
    }
	

	// --------------------------------------------------

    private static Method lookupMethod(Class target, String name,
                                       Class[] argClasses, Object[] argValues) throws NoSuchMethodException {
        // first try for exact match
        try {
            Method m = target.getMethod(name, argClasses);
            return m;
        } catch (NoSuchMethodException e) {
            if (argClasses.length == 0) { // if no args & no exact match, out of luck
                return null;
            }
        }

        // go the more complicated route
        Method[] methods = target.getMethods();
        Vector goodMethods = new Vector();
        for (int i = 0; i != methods.length; i++) {
            if (name.equals(methods[i].getName()) &&
                    matchClasses(methods[i].getParameterTypes(), argClasses))
                goodMethods.addElement(methods[i]);
        }
        switch (goodMethods.size()) {
            case 0:
                // no methods have been found checking for assignability
                // and (int -> long) conversion. One last chance:
                // looking for compatible methods considering also
                // type conversions:
                //    double --> float
                // (the first found is used - no most specific
                //  method algorithm is applied )

                for (int i = 0; i != methods.length; i++) {
                    if (name.equals(methods[i].getName())) {
                        Class[] types = methods[i].getParameterTypes();
                        Object[] val = matchClasses(types, argClasses, argValues);
                        if (val != null) {
                            // found a method compatible
                            // after type conversions
                            for (int j = 0; j < types.length; j++) {
                                argClasses[j] = types[j];
                                argValues[j] = val[j];
                            }
                            return methods[i];
                        }
                    }
                }

                return null;
            case 1:
                return (Method) goodMethods.firstElement();
            default:
                return mostSpecificMethod(goodMethods);
        }
    }

    private static Constructor lookupConstructor(Class target, Class[] argClasses, Object[] argValues) throws NoSuchMethodException {
        // first try for exact match
        try {
            return target.getConstructor(argClasses);
        } catch (NoSuchMethodException e) {
            if (argClasses.length == 0) { // if no args & no exact match, out of luck
                return null;
            }
        }

        // go the more complicated route
        Constructor[] constructors = target.getConstructors();
        Vector goodConstructors = new Vector();
        for (int i = 0; i != constructors.length; i++) {
            if (matchClasses(constructors[i].getParameterTypes(), argClasses))
                goodConstructors.addElement(constructors[i]);
        }
        switch (goodConstructors.size()) {
            case 0:
                // no constructors have been found checking for assignability
                // and (int -> long) conversion. One last chance:
                // looking for compatible methods considering also
                // type conversions:
                //    double --> float
                // (the first found is used - no most specific
                //  method algorithm is applied )

                for (int i = 0; i != constructors.length; i++) {
                    Class[] types = constructors[i].getParameterTypes();
                    Object[] val = matchClasses(types, argClasses, argValues);
                    if (val != null) {
                        // found a method compatible
                        // after type conversions
                        for (int j = 0; j < types.length; j++) {
                            argClasses[j] = types[j];
                            argValues[j] = val[j];
                        }
                        return constructors[i];
                    }
                }

                return null;
            case 1:
                return (Constructor) goodConstructors.firstElement();
            default:
                return mostSpecificConstructor(goodConstructors);
        }
    }

    // 1st arg is from method, 2nd is actual parameters
    private static boolean matchClasses(Class[] mclasses, Class[] pclasses) {
        if (mclasses.length == pclasses.length) {
            for (int i = 0; i != mclasses.length; i++) {
                if (!matchClass(mclasses[i], pclasses[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean matchClass(Class mclass, Class pclass) {
        boolean assignable = mclass.isAssignableFrom(pclass);
        if (assignable) {
            return true;
        } else {
            if (mclass.equals(java.lang.Long.TYPE) && (pclass.equals(java.lang.Integer.TYPE))) {
                return true;
            }
        }
        return false;
    }

    private static Method mostSpecificMethod(Vector methods) throws NoSuchMethodException {
        for (int i = 0; i != methods.size(); i++) {
            for (int j = 0; j != methods.size(); j++) {
                if ((i != j) &&
                        (moreSpecific((Method) methods.elementAt(i), (Method) methods.elementAt(j)))) {
                    methods.removeElementAt(j);
                    if (i > j) i--;
                    j--;
                }
            }
        }
        if (methods.size() == 1)
            return (Method) methods.elementAt(0);
        else
            throw new NoSuchMethodException(">1 most specific method");
    }

    // true if c1 is more specific than c2
    private static boolean moreSpecific(Method c1, Method c2) {
        Class[] p1 = c1.getParameterTypes();
        Class[] p2 = c2.getParameterTypes();
        int n = p1.length;
        for (int i = 0; i != n; i++) {
            if (!matchClass(p2[i], p1[i])) {
                return false;
            }
        }
        return true;
    }

    private static Constructor mostSpecificConstructor(Vector constructors) throws NoSuchMethodException {
        for (int i = 0; i != constructors.size(); i++) {
            for (int j = 0; j != constructors.size(); j++) {
                if ((i != j) &&
                        (moreSpecific((Constructor) constructors.elementAt(i), (Constructor) constructors.elementAt(j)))) {
                    constructors.removeElementAt(j);
                    if (i > j) i--;
                    j--;
                }
            }
        }
        if (constructors.size() == 1)
            return (Constructor) constructors.elementAt(0);
        else
            throw new NoSuchMethodException(">1 most specific constructor");
    }

    // true if c1 is more specific than c2
    private static boolean moreSpecific(Constructor c1, Constructor c2) {
        Class[] p1 = c1.getParameterTypes();
        Class[] p2 = c2.getParameterTypes();
        int n = p1.length;
        for (int i = 0; i != n; i++) {
            if (!matchClass(p2[i], p1[i])) {
                return false;
            }
        }
        return true;
    }


    // Checks compatibility also considering explicit type conversion.
    // The method returns the argument values, since they could be changed
    // after a type conversion.
    //
    // In particular the check must be done for the DEFAULT type of tuProlog,
    // that are int and double; so
    //   (required X, provided a DEFAULT -
    //        with DEFAULT to X conversion 'conceivable':
    //        for instance *double* to *int* is NOT considered good
    //
    //   required a float,  provided an  int  OK
    //   required a double, provided a   int  OK
    //   required a long,   provided a   int ==> already considered by
    //                                   previous match test
    //   required a float,  provided a   double OK
    //   required a int,    provided a   double => NOT CONSIDERED
    //   required a long,   provided a   double => NOT CONSIDERED
    //
    private static Object[] matchClasses(Class[] mclasses, Class[] pclasses, Object[] values) {
        if (mclasses.length == pclasses.length) {
            Object[] newvalues = new Object[mclasses.length];

            for (int i = 0; i != mclasses.length; i++) {
                boolean assignable = mclasses[i].isAssignableFrom(pclasses[i]);
                if (assignable ||
                        (mclasses[i].equals(java.lang.Long.TYPE) && pclasses[i].equals(java.lang.Integer.TYPE))) {
                    newvalues[i] = values[i];
                } else if (mclasses[i].equals(java.lang.Float.TYPE) &&
                        pclasses[i].equals(java.lang.Double.TYPE)) {
                    // arg required: a float, arg provided: a double
                    // so we need an explicit conversion...
                    newvalues[i] = new java.lang.Float(((java.lang.Double) values[i]).floatValue());
                } else if (mclasses[i].equals(java.lang.Float.TYPE) &&
                        pclasses[i].equals(java.lang.Integer.TYPE)) {
                    // arg required: a float, arg provided: an int
                    // so we need an explicit conversion...
                    newvalues[i] = new java.lang.Float(((java.lang.Integer) values[i]).intValue());
                } else if (mclasses[i].equals(java.lang.Double.TYPE) &&
                        pclasses[i].equals(java.lang.Integer.TYPE)) {
                    // arg required: a double, arg provided: an int
                    // so we need an explicit conversion...
                    newvalues[i] = new java.lang.Double(((java.lang.Integer) values[i]).doubleValue());
                } else if (values[i] == null && !mclasses[i].isPrimitive()) {
                    newvalues[i] = null;
                } else {
                    return null;
                }
            }
            return newvalues;
        } else {
            return null;
        }
    }

	/**
	 * Convert a Jason term into a CArtAgO/Java Object
	 * 
	 * @param t Jason term
	 * @param lib Java library - each agent has its own one
	 * @return
	 */
	public Object termToObject(Term t){
		//System.out.println(">> "+t);
	    if (t instanceof VarTerm){
			VarTerm var = (VarTerm)t;
			if (!var.isVar()){  // isVar means is a variable AND is not bound (see Jason impl)
				return null;// should not happen! termToObject(var.getValue());
			} else {
				return new OpFeedbackParam<Object>();
			}
	    } else if (t.isAtom()) {
			Atom t2 = (Atom)t;
			if (t2.equals(Atom.LTrue)){
				return Boolean.TRUE;
			} else if (t2.equals(Atom.LFalse)){
				return Boolean.FALSE;
			} else {
				Object obj = getObject(t2);
				if (obj != null){
					return obj;
				} else {
					return t2.toString();
				}
			}
	    } else if (t.isNumeric()) {
			NumberTerm nt = (NumberTerm)t;
			double d = 0;
            try {
                d = nt.solve();
            } catch (NoValueException e) {
                e.printStackTrace();
            }
			if (((byte)d)==d){
				return (byte)d; 
			} else if (((int)d)==d){
				return (int)d;
			} else if (((float)d)==d){
				return (float)d;
			} else if (((long)d)==d){
				return (long)d;
			} else {
				return d;
			}
		} else if (t.isString()) {
			//System.out.println("STRING "+t);
			return ((StringTerm)t).getString(); //(t.toString()).substring(1,t.toString().length()-1);
		} else if (t.isList()) {
			//System.out.println("LIST "+t);
			ListTerm lt = (ListTerm)t;
			int i = 0;
			Object[] list = new Object[lt.size()];
			for (Term t1: lt){
				list[i++] = termToObject(t1);
			}
			return list;
		} else if (t instanceof ObjectTerm){
			return ((ObjectTerm)t).getObject();
		} else {
			return t.toString();
		}
	}

	
	/**
	 * Convert Java Object into a Jason term
	 */
    public Term objectToTerm(Object value) throws Exception {
		if (value == null){
			return ASSyntax.createVar("_");
		} else if (value instanceof Term) {
		    return (Term)value;
		} else if (value instanceof OpFeedbackParam<?>){
			return objectToTerm(((OpFeedbackParam<?>)value).get());
		} else if (value instanceof Number) {
            try {
                return ASSyntax.parseNumber(value.toString());
            } catch (Exception ex){
                return ASSyntax.createString(value.toString());
            }
        } else if (value instanceof String) {
            return ASSyntax.createString(value.toString());
        } else if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue() ? Literal.LTrue : Literal.LFalse;
            //Tricky solution that try to avoiding a "possible" Java bug in converting
            //an array to a collection Arrays.asList(value);
        } else if (value.getClass().isArray()) { 
        	ListTerm l = new ListTermImpl();
            ListTerm tail = l;
            int lenght = java.lang.reflect.Array.getLength(value);
            for (int i=0; i< lenght; i++)
                tail = tail.append(objectToTerm(java.lang.reflect.Array.get(value,i)));
            return l;             
		} else if (value instanceof Map) {
            ListTerm l = new ListTermImpl();
            ListTerm tail = l;
            Map m = (Map)value;
            for (Object k: m.keySet()) {
                Term pair = ASSyntax.createStructure("map",
                        objectToTerm(k),
                        objectToTerm(m.get(k)));
                tail = tail.append(pair);
            }
            return l;             
		} else if (value instanceof ToProlog) {
	        return ASSyntax.parseTerm(((ToProlog)value).getAsPrologStr());
	    //Collection conversion: intentionally not supported yet
		/*}  else if (value instanceof Collection) {
            ListTerm l = new ListTermImpl();
            ListTerm tail = l;
            for (Object e: (Collection)value)
                tail = tail.append(objectToTerm(e));
            return l; */            
        }  else if (value instanceof Tuple) {
        	Tuple t = (Tuple)value;
        	Structure st =  ASSyntax.createStructure(t.getLabel()); 
        	for (int i = 0; i < t.getNArgs(); i++){
        		st.addTerm(objectToTerm(t.getContent(i)));		
        	}
        	return st;
        }
	    return registerDynamic(value);
	}
	
	public Term[] objectArray2termArray(Object[] values) throws Exception {
        Term[] result = new Term[values.length];
        for (int i=0; i<values.length; i++)
            result[i] = objectToTerm(values[i]);
        return result;
    }

	public Tuple termToTuple(Term t) {
    	if (t.isAtom()){
    		return new Tuple(((Atom)t).getFunctor());
    	} else if (t.isStructure()){
    		Structure st = (Structure)t;
    		Term[] ta = st.getTermsArray();
    		Object[] objs = new Object[ta.length];
    		for (int i = 0; i < ta.length; i++){
    			objs[i] = termToObject(ta[i]);
    		}
    		return new Tuple(st.getFunctor(),objs);
    	} else throw new IllegalArgumentException();
    }
        
    public Term tupleToTerm(Tuple t) throws Exception {
    	Structure st = new Structure(t.getLabel());
    	for (Object obj: t.getContents()){
    		st.addTerm(objectToTerm(obj));
    	}
    	return st;
    }

    public Literal tupleToLiteral(Tuple t) throws Exception {
    	Structure st = new Structure(t.getLabel());
    	for (Object obj: t.getContents()){
    		st.addTerm(objectToTerm(obj));
    	}
    	return st;
    }
    
    /**
     * Signature class mantains information
     * about type and value of a method
     * arguments
     */
    class Signature implements Serializable {
        Class[] types;
        Object[] values;

        public Signature(Object[] v, Class[] c) {
            values = v;
            types = c;
        }

        public Class[] getTypes() {
            return types;
        }

        Object[] getValues() {
            return values;
        }

        public String toString() {
            String st = "";
            for (int i = 0; i < types.length; i++) {
                st = st + "\n  Argument " + i + " -  VALUE: " + values[i] + " TYPE: " + types[i];
            }
            return st;
        }
    }


}
