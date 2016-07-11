package cartago.infrastructure.lipermi;

import lipermi.exception.LipeRMIException;
import lipermi.handler.CallHandler;
import cartago.CartagoEvent;
import cartago.ICartagoCallback;

/**
 * 
 * @author mguidi
 *
 */
public class CartagoCallbackRemote implements ICartagoCallbackRemote {

	private static final long serialVersionUID = 1L;
	private ICartagoCallback mCallback;
	private CallHandler mCallHander;
	
	public CartagoCallbackRemote(ICartagoCallback callback, CallHandler callHandler) throws LipeRMIException {
		mCallback = callback;
		mCallHander = callHandler;
		mCallHander.exportObject(ICartagoCallbackRemote.class, this);
		
	}
	
	@Override
	public void notifyCartagoEvent(CartagoEvent ev) {
		mCallback.notifyCartagoEvent(ev);
	}
	
	public void invalidateObject() {
		// TODO 
	}

}
