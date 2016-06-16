package cartago.tools;


import java.util.*;
import java.io.*;

import cartago.*;

public class Latch extends Artifact {
	
	boolean isSet;
	
	@OPERATION void init(){
		isSet = false;
	}
	
	@OPERATION void set(){
		isSet = true;
	}	

	@OPERATION(guard="isSet") void waitForSet(){}	
	
	@GUARD boolean isSet(){
		return isSet;
	}
}
