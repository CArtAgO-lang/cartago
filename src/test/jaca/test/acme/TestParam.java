package acme;

import java.lang.reflect.*;
import cartago.*;

public class TestParam {

	 class ReflectionApi { 
		 
	    public void withoutClass(A<Integer> list) { } 
	    
	}
	
	public static void main(String[] args) {

		    A value = new A();
		    value.set(1);
		    
			// obj.set(new ArtifactId());
			try {
				// For brevity's sake the code has been stripped of exception handling and does no check before casting
				Method method = ReflectionApi.class.getDeclaredMethod("withoutClass", A.class); 
				Type[] parameterTypes = method.getGenericParameterTypes(); 
				Type parameterType = parameterTypes[0]; 
				ParameterizedType parameterizedType = (ParameterizedType) parameterType; 
				Type[] typeArguments = parameterizedType.getActualTypeArguments(); 
				for (Type typeArgument : typeArguments) { 
				    System.out.println(typeArgument.getTypeName()); 
				}
			    
					    
		        // production code should handle these exceptions more gracefully
			} catch (Exception x) {
			    x.printStackTrace();
			} 
		
	}

}
