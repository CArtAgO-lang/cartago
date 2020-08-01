package cartago.tools;
import cartago.*;

@ARTIFACT_INFO(
  manual_file = "cartago/tools/Console.man"
) public class Console extends Artifact {

	/**
	 * <p>Print a sequence of messages on the console</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>arg (Object) - msg to print</li>
	 * </ul></p>
	 * 
	 */	
	@OPERATION void print(Object... args){
		for (Object st: args){
			System.out.print(st);
		}
	}


	/**
	 * <p>Print a sequence of messages on the console</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>arg (Object) - msg to print</li>
	 * </ul></p>
	 * 
	 */	
	@OPERATION void println(Object... args){
		for (Object st: args){
			System.out.print(st);
		}
		System.out.println();
	}

	/**
	 * <p>Print a sequence of messages on the console</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>arg (Object) - msg to print</li>
	 * </ul></p>
	 * 
	 */	
	@OPERATION void printWithAgName(Object... args){
		System.out.print("["+getCurrentOpAgentId().getAgentName()+"] ");
		for (Object st: args){
			System.out.print(st);
		}
	}


	/**
	 * <p>Print a sequence of messages on the console</p>
	 * 
	 * <p>Parameters:
	 * <ul>
	 * <li>arg (Object) - msg to print</li>
	 * </ul></p>
	 * 
	 */	
	@OPERATION void printlnWithAgName(Object... args){
		System.out.print("["+getCurrentOpAgentId().getAgentName()+"] ");
		for (Object st: args){
			System.out.print(st);
		}
		System.out.println();
	}

}
