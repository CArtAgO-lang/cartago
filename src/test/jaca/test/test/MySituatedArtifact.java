package test;

import cartago.*;

public class MySituatedArtifact extends Artifact {
	
	void init(double x, double y, double observabilityRadius){
		setupPosition(new P2d(x,y),observabilityRadius);
		defineObsProperty("count",1);
		defineObsProperty("pos",x,y);
		log("pos "+x+" "+y);
	}
	
	@OPERATION void inc(){
		updateObsProperty("count", this.getObsProperty("count").intValue()+1);
	}
	
	@OPERATION void moveTo(double x, double y){
		ObsProperty prop = getObsProperty("pos");
		prop.updateValue(0, x);
		prop.updateValue(1, y);
		this.updatePosition(new P2d(x,y));
		log("moved to " + x + " " + y);
	}
}
