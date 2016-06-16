package cartago;

/**
 * Basic abstract class representing factories to instantiate artifacts,
 * managing artifact types
 * 
 * @author aricci
 *
 */
public abstract class ArtifactFactory implements java.io.Serializable {
	private String name;
	
	public ArtifactFactory(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	abstract public Artifact createArtifact(String templateName) throws CartagoException ;
}
