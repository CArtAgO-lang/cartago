package cartago;

public class DefaultArtifactFactory extends ArtifactFactory {
	
	public DefaultArtifactFactory(){
		super("default");
	}
	
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class cl = Class.forName(templateName);
			return (Artifact)cl.newInstance();
		} catch(Exception ex){
			throw new CartagoException("Template not found: "+templateName);
		}
	}
}
