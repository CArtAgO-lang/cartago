package test;

import cartago.*;

public class CommonArtifact extends Artifact {

	int v = 0;
	
	@OPERATION void op(int x, OpFeedbackParam<Integer> res){
		v += x;
		res.set(v);
	}
}
