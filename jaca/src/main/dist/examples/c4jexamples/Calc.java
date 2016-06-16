package c4jexamples;

import cartago.*;

public class Calc extends Artifact {

	@OPERATION void sum(double a, double b, OpFeedbackParam<Double> sum){
		sum.set(a+b);
	}

	@OPERATION void sumAndSub(double a, double b, OpFeedbackParam<Double> sum, OpFeedbackParam<Double> sub){
		sum.set(a+b);
		sub.set(a-b);
	}
}