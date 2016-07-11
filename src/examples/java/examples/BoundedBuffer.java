package examples;

import java.util.*;

import cartago.*;

public class BoundedBuffer extends Artifact {

	private LinkedList<Object> items;
	private int nmax;
	
	void init(int nmax){
		items = new LinkedList<Object>();
		defineObsProperty("n_items",0);
		this.nmax = nmax;
	}
	
	@OPERATION(guard="bufferNotFull") void put(Object obj){
		items.add(obj);
		getObsProperty("n_items").updateValue(items.size());
	}
	
	@OPERATION(guard="itemAvailable") void get(OpFeedbackParam<Object> res){
		Object item = items.removeFirst();
		res.set(item);
		getObsProperty("n_items").updateValue(items.size());
		
	}
	
	@GUARD boolean itemAvailable(OpFeedbackParam<Object> res){
		return items.size() > 0;
	}

	@GUARD boolean bufferNotFull(Object obj){
		return items.size() < nmax;
	}		
}
