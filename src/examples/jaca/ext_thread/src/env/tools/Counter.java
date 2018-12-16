// CArtAgO artifact code for project ext_thread

package tools;

import cartago.*;

public class Counter extends Artifact {
	void init(int initialValue) {
		defineObsProperty("count", initialValue);
		
		// creates a thread that updates the count obs property every second
		new Updater(this).start();
	}

	// to be called by agents
	@OPERATION void inc() {
		ObsProperty prop = getObsProperty("count");
		prop.updateValue(prop.intValue()+1);
		signal("tick");
	}
	
	// to be called by an external thread
	void internalInc() {
		beginExternalSession();
		
		ObsProperty prop = getObsProperty("count");
		prop.updateValue(prop.intValue()+1);
		
		endExternalSession(true);
	}
}

