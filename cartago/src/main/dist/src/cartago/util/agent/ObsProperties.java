package cartago.util.agent;

import cartago.*;

public class ObsProperties {

	private ArtifactObsProperty[] props;
	
	ObsProperties(){
	}
	
	void setValue(ArtifactObsProperty[] props){
		this.props = props;
	}
	
	public ArtifactObsProperty getProperty(String name){
		for (ArtifactObsProperty obs: props){
			if (obs.getName().equals(name)){
				return obs;
			}
		}
		return null;
	}
	
}
