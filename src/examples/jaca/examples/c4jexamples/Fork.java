package c4jexamples;

import cartago.*;

public class Fork extends Artifact {

	void init(){
		defineObsProperty("available",true);
	}
	    
	@OPERATION void grab(){
		ObsProperty prop = getObsProperty("available");
		if (prop.booleanValue()){
			failed("not_available");
		} else {
			prop.updateValue(false);
		}
	}

	@OPERATION void release(){
		ObsProperty prop = getObsProperty("available");
		if (prop.booleanValue()){
			prop.updateValue(true);
		} else {
			failed("not_grabbed");
		}
	}

	@GUARD boolean isAvailable(){
		return getObsProperty("available").booleanValue();
	}
	
	@OPERATION void grabAsap(){
		await("isAvailable");
		getObsProperty("available").updateValue(true);
	}

}