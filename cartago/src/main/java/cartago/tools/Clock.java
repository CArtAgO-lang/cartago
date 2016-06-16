package cartago.tools;

import cartago.*;

/**
 * A simple clock artifact.
 * 
 * @author aricci
 *
 */
public class Clock extends Artifact {

	private boolean stopped;
	private int nwaits;
	
	@OPERATION void init(){
		defineObsProperty("nticks",0);
		stopped = false;
		nwaits = 10;
	}
		
	/**
	 * Start the clock.
	 * 
	 * <p>Events generated:
	 * <ul>
	 * <li>tick - a clock tick</li>
	 * </ul>
	 * </p>
	 */
	@OPERATION void start(){
		stopped = false;
		execInternalOp("ticketing");
	}

	@OPERATION void setFrequency(int hz){
		nwaits = 1000/hz;
	}
	/**
	 * Stop the clock.
	 */
	@OPERATION void stop(){
		stopped = true;
	}

	@INTERNAL_OPERATION void ticketing(){
		while (!stopped){
			ObsProperty prop = getObsPropertyByTemplate("nticks");
			prop.updateValue(prop.intValue() + nwaits);
			signal("tick");
			await_time(nwaits);
		}
	}	
}
