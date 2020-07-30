package acme;

import cartago.*;

public class MyArtifactA extends Artifact {


	@OPERATION void compute(int x, OpFeedbackParam<Integer> y1, OpFeedbackParam<Integer> y2){
		log("computing..."+x+" "+y1+" "+y2);
		try {
			y1.set(x+1);
			y2.set(x*2);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	@OPERATION void testFail(){		
		failed("Failure msg", "reason", "test",303);
	}
	
	
	@OPERATION void compute(){
		try {
			Thread.sleep(1000);
		} catch (Exception ex){}
		signal("eventOne",1);
		try {
			Thread.sleep(2000);
		} catch (Exception ex){}
		signal("eventTwo",new MyData(2));
	}
	
	public class MyData {
		private int data;
		public MyData(int v){
			data = v;
		}
		public int getValue(){
			return data;
		}
	}
}
