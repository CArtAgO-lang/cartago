package cartago;

import java.net.*;

public class URLArtifactFactory extends ArtifactFactory {
	
	private static final long serialVersionUID = -1346955866965085723L;
	public String address;
	public URLClassLoader cloader;
	
	public URLArtifactFactory(String name, URL address){
		super(name);
		cloader = new URLClassLoader(new java.net.URL[] {address},this.getClass().getClassLoader());
	}
	
	@Override
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class<?> cl = cloader.loadClass (templateName);
			return (Artifact)cl.newInstance();
		} catch(Exception ex){
			throw new CartagoException("Template not found: "+templateName+" at "+address);
		}
	}

	@Override
	public Artifact createDynamicArtifact(String templateName, String sourceDir) throws CartagoException {
		return null;
	}
}
