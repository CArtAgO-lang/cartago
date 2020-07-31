package acme;
import cartago.*;

public class ArtifactWithDynamicOps extends Artifact {
	
	void init(){
		defineObsProperty("my_count",0);
		
		try {
			this.defineNewOp("inc", (Artifact art, Object[] params) -> {
				ObsProperty prop = getObsProperty("my_count");
				int newValue = prop.intValue() + 1;
				prop.updateValue(newValue);
			});

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	    
	@OPERATION void extend(){
		try {
			this.defineNewOp("add", (Artifact art, Object[] params) -> {
				try {
					int dv = ((Number) params[0]).intValue();
					ObsProperty prop = getObsProperty("my_count");
					int newValue = prop.intValue() + dv;
					prop.updateValue(newValue);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
