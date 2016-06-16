package cartago;

/**
 * Artifact no more available exception
 * 
 * @author aricci
 *
 */
public class NoArtifactException extends CartagoException {

	private String opName;
	
	public NoArtifactException(String opName){
		this.opName = opName;
	}
	
	public String getOpName(){
		return opName;
	}
}
