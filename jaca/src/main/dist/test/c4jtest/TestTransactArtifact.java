package c4jtest;

import cartago.*;

public class TestTransactArtifact extends Artifact {

	void init(){
		defineObsProperty("a",1);
		defineObsProperty("b",1);
	}

	@OPERATION void update1(){
		log("updating..");
		ObsProperty prop1 = getObsProperty("a");
		prop1.updateValue(prop1.intValue()+1);
		ObsProperty prop2 = getObsProperty("b");
		prop2.updateValue(prop2.intValue()+1);
		log("updated..");
		this.delay(2000);
		log("op completed");
	}

	@OPERATION void update2(){
		log("updating..");
		ObsProperty prop1 = getObsProperty("a");
		prop1.updateValue(prop1.intValue()+1);
		signal("first_step_done");
		ObsProperty prop2 = getObsProperty("b");
		prop2.updateValue(prop2.intValue()+1);
		this.delay(2000);
		log("op completed");
	}

	@OPERATION void update3(){
		log("updating..");
		ObsProperty prop1 = getObsProperty("a");
		prop1.updateValue(50);
		ObsProperty prop2 = getObsProperty("b");
		prop2.updateValue(50);
		failed("test");
	}
	
	@OPERATION void update4(){
		log("two steps");
		ObsProperty prop1 = getObsProperty("a");
		prop1.updateValue(prop1.intValue()+20);
		log("first step done");
		delay(4000);
		await("valueAchieved");
		log("doing the second step");
		ObsProperty prop2 = getObsProperty("b");
		prop2.updateValue(50);
	}

	@GUARD boolean valueAchieved(){
		ObsProperty prop1 = getObsProperty("a");
		return prop1.intValue() > 40;
	}
}
