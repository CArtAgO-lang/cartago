package test;

import cartago.*;
import jaca.*;

import java.util.*;


public class TestToProlog extends Artifact {

	private LinkedList<Object> items;
	
	@OPERATION void init(int nmax) {
		items = new LinkedList<Object>();
        for (int i=0; i<nmax; i++) 
            items.add(i);

        signal("all_items",items);
        
        Map m = new HashMap();
        m.put("a", new Client("bob"));
        m.put("b", new Client("carlos"));
        m.put("c", new Client("tom"));
        defineObsProperty("test_map",m);
	}
	
}

class Client implements ToProlog {

    int id;
    String name;
    
    static int count = 0;
    
    Client(String n) {
        id = (count++);
        name = n;
    }
    
    public String getAsPrologStr() {
        return "client("+id+",\""+name+"\",[a,b,c]);";
    }
}

