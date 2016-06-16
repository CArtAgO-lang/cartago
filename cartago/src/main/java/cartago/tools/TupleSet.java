package cartago.tools;

import java.util.*;

import cartago.*;
import cartago.tools.*;

class TupleSet {

	LinkedList<Tuple> list;
	
	public TupleSet(){
		list = new LinkedList<Tuple>();
	}

	public void add(Tuple t){
		list.add(t);
	}
	
	public Tuple removeMatching(TupleTemplate tt){
		ListIterator<Tuple> it = list.listIterator();
		while (it.hasNext()){
			Tuple t = it.next();
			if (tt.match(t)){
				it.remove();
				return t;
			}
		}
		return null;
	}

	public Tuple readMatching(TupleTemplate tt){
		for (Tuple t:list){
			if (tt.match(t)){
				return t;
			}
		}
		return null;
	}
	
	public boolean hasTupleMatching(TupleTemplate tt){
		for (Tuple t:list){
			if (tt.match(t)){
				return true;
			}
		}
		return false;
	}
}
