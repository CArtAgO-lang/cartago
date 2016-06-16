package cartago;

public class ExtClassLoaderArtifactFactory extends ArtifactFactory {
	
	private ClassLoader cloader;
	
	public ExtClassLoaderArtifactFactory(ClassLoader cl){
		super("default");
		this.cloader = cl;
	}
	
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class cl = cloader.loadClass(templateName);
			return (Artifact)cl.newInstance();
		} catch(Exception ex){
			throw new CartagoException("Template not found: "+templateName+" with "+cloader);
		}
	}
}
