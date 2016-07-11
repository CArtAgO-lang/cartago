package c4jtest;
import cartago.*;

public class MyArtifactB extends Artifact {
	
	void init(){
		defineObsProperty("complex_prop","test",12);
	}
	
	@OPERATION void addNewProp(String name, int value){
		defineObsProperty(name,value);
		log("new prop added: "+name);
	}

	@OPERATION void update(int index, Object value){
		getObsProperty("complex_prop").updateValue(index,value);
	}
	
}
