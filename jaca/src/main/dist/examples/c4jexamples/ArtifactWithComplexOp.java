package c4jexamples;

import cartago.*;

public class ArtifactWithComplexOp extends Artifact {
	
	int internalCount;

	void init(){
		internalCount = 0;
	}
	
	@OPERATION void complexOp(int ntimes){
		doSomeWork();
		signal("step1_completed");
		await("myCondition", ntimes);
		signal("step2_completed",internalCount);
	}
	
	@GUARD boolean myCondition(int ntimes){
		return internalCount >= ntimes;
	}
	
	@OPERATION void update(int delta){
		internalCount+=delta;
	}
	
	private void doSomeWork(){
      this.delay(4000);
    }
}
