package cartago;

/**
 * Class representing an action feedback parameter, i.e. an output parameter of an operation
 * 
 * It is parameterized on the type of the parameter
 * 
 * @author aricci
 *
 * @param <T>
 */
public class OpFeedbackParam<T> implements java.io.Serializable {

	private T value;
	
	public OpFeedbackParam(){
		
	}
	
	/**
	 * Set the value of the parameter
	 * 
	 * @param t
	 */
	public void set(T t){
		value = t;
	}
	
	/**
	 * Get the value of the parameter
	 * 
	 * @return
	 */
	public T get(){
		return value;
	}
	
	public void copyFrom(OpFeedbackParam<?> param){
		value = ((OpFeedbackParam<T>)param).value;
	}
}
