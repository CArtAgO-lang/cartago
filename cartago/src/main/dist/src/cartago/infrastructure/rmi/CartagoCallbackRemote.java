package cartago.infrastructure.rmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import cartago.CartagoEvent;
import cartago.ICartagoCallback;

/**
 * CArtAgO Callback - agent side.
 * 
 * Accessed by the proxy, which is on the workspace side.
 * 
 * @author aricci
 *
 */
public class CartagoCallbackRemote extends UnicastRemoteObject implements ICartagoCallbackRemote {

	private ICartagoCallback context;
	
	public CartagoCallbackRemote(ICartagoCallback context) throws RemoteException {
		this.context = context;
	}

	public void notifyCartagoEvent(CartagoEvent ev) throws RemoteException {
		context.notifyCartagoEvent(ev);
	}
	
}
