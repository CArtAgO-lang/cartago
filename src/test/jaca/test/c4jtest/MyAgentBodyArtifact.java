package c4jtest;

import cartago.*;

public class MyAgentBodyArtifact extends AgentBodyArtifact {
	
	void init(double x, double y, double observabilityRadius, double observingRadius){
		setupPosition(new P2d(x,y),observabilityRadius,observingRadius);
		defineObsProperty("pos",x,y);
		log("pos "+x+" "+y);
	}
	
	@OPERATION void moveTo(double x, double y){
		ObsProperty prop = getObsProperty("pos");
		prop.updateValue(0, x);
		prop.updateValue(1, y);
		updatePosition(new P2d(x,y));
	}
}
