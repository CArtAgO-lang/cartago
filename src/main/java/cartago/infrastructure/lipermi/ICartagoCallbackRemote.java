package cartago.infrastructure.lipermi;

import java.io.Serializable;

import cartago.CartagoEvent;

/**
 * 
 * @author mguidi
 *
 */
public interface ICartagoCallbackRemote extends Serializable {

	void notifyCartagoEvent(CartagoEvent ev);
	
	
}
