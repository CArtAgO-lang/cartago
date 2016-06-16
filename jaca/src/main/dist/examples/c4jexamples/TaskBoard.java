package c4jexamples;
import cartago.*;

public class TaskBoard extends Artifact {
	private int taskId;
	
	void init(){
		taskId = 0;
	}

	@OPERATION void announce(String taskDescr, int duration, OpFeedbackParam<String> id){
		taskId++;
		try {
			String artifactName = "cnp_board_"+taskId;
			makeArtifact(artifactName, "c4jexamples.ContractNetBoard", new ArtifactConfig(taskDescr,duration));
			defineObsProperty("task", taskDescr, artifactName);
			id.set(artifactName);
		} catch (Exception ex){
			failed("announce_failed");
		}		
	}

	@OPERATION void clear(String id){
		String artifactName = "cnp_board_"+taskId;
		this.removeObsPropertyByTemplate("task", null, artifactName);
	}
}
