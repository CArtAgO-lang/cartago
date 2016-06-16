package examples;

public class FlatCountObject {

	private int count;
	
	public FlatCountObject(int v){
		count = v;
	}

	public FlatCountObject(){
		count = 0;
	}
	
	public void inc(){
		count++;
	}

	public void inc(int dv){
		count+=dv;
	}
	
	public int getValue(){
		return count;
	}

}
