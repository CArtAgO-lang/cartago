package cartago.infrastructure.lipermi;

import cartago.CartagoEvent;
import cartago.ICartagoCallback;

/**
 * 
 * @author mguidi
 *
 */
public class CartagoCallbackProxy implements ICartagoCallback {
	
	private static final long serialVersionUID = 1L;
	private ICartagoCallbackRemote mCallback;
	
	public CartagoCallbackProxy(ICartagoCallbackRemote callback){
		mCallback = callback;
	}
	
	public void notifyCartagoEvent(CartagoEvent ev){
		mCallback.notifyCartagoEvent(ev);
	}
	
}
